/*
 * Copyright (c) 2010-2023 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 */
package org.linphone.ui.main.chat.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.annotation.UiThread
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.linphone.LinphoneApplication.Companion.coreContext
import org.linphone.R
import org.linphone.contacts.getListOfSipAddressesAndPhoneNumbers
import org.linphone.core.tools.Log
import org.linphone.databinding.ChatListFragmentBinding
import org.linphone.ui.fileviewer.FileViewerActivity
import org.linphone.ui.fileviewer.MediaViewerActivity
import org.linphone.ui.main.MainActivity.Companion.ARGUMENTS_CONVERSATION_ID
import org.linphone.ui.main.chat.adapter.ConversationsListAdapter
import org.linphone.ui.main.chat.model.ConversationModel
import org.linphone.ui.main.chat.viewmodel.ConversationsListViewModel
import org.linphone.ui.main.contacts.model.ContactNumberOrAddressClickListener
import org.linphone.ui.main.contacts.model.ContactNumberOrAddressModel
import org.linphone.ui.main.contacts.model.NumberOrAddressPickerDialogModel
import org.linphone.ui.main.fragment.AbstractMainFragment
import org.linphone.utils.ConfirmationDialogModel
import org.linphone.utils.DialogUtils
import org.linphone.utils.Event
import org.linphone.utils.LinphoneUtils
import org.linphone.utils.RecyclerViewHeaderDecoration

@UiThread
class ConversationsListFragment : AbstractMainFragment() {
    companion object {
        private const val TAG = "[Conversations List Fragment]"

        const val ARG_CHAT_LIST_MODE = "chatListMode"
        const val CHAT_MODE_ALL = 0
        const val CHAT_MODE_ONE_TO_ONE = 1
        const val CHAT_MODE_GROUP = 2
    }

    private lateinit var binding: ChatListFragmentBinding

    private lateinit var listViewModel: ConversationsListViewModel

    private lateinit var adapter: ConversationsListAdapter

    private var bottomSheetDialog: BottomSheetDialogFragment? = null

    private var chatListMode: Int = CHAT_MODE_ALL

    private val numberOrAddressClickListener = object : ContactNumberOrAddressClickListener {
        @UiThread
        override fun onClicked(model: ContactNumberOrAddressModel) {
            coreContext.postOnCoreThread {
                val address = model.address
                if (address != null) {
                    Log.i("$TAG Creating 1-1 conversation with to [${address.asStringUriOnly()}]")
                    listViewModel.createOneToOneChatRoomWith(address)
                }
            }
        }

        override fun onLongPress(model: ContactNumberOrAddressModel) { }
    }

    private val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            Log.i("$TAG [$itemCount] added, scrolling to top")
            binding.conversationsList.scrollToPosition(0)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            Log.i("$TAG [$itemCount] moved, scrolling to top")
            binding.conversationsList.scrollToPosition(0)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) { }
    }

    override fun onDefaultAccountChanged() {
        Log.i(
            "$TAG Default account changed, updating avatar in top bar & re-computing conversations"
        )
        listViewModel.filter()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (
            findNavController().currentDestination?.id == R.id.startConversationFragment ||
            findNavController().currentDestination?.id == R.id.meetingWaitingRoomFragment
        ) {
            return AnimationUtils.loadAnimation(activity, R.anim.hold)
        }

        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ConversationsListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatListFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatListMode = arguments?.getInt(ARG_CHAT_LIST_MODE, CHAT_MODE_ALL) ?: CHAT_MODE_ALL
        Log.i("$TAG Chat list mode received = [$chatListMode]")

        listViewModel = ViewModelProvider(this)[ConversationsListViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = listViewModel
        observeToastEvents(listViewModel)

        binding.conversationsList.setHasFixedSize(true)
        binding.conversationsList.layoutManager = LinearLayoutManager(requireContext())
        binding.conversationsList.outlineProvider = outlineProvider
        binding.conversationsList.clipToOutline = true

        val headerItemDecoration = RecyclerViewHeaderDecoration(requireContext(), adapter)
        binding.conversationsList.addItemDecoration(headerItemDecoration)

        adapter.conversationLongClickedEvent.observe(viewLifecycleOwner) {
            it.consume { model ->
                val modalBottomSheet = ConversationDialogFragment(
                    model.isMuted.value == true,
                    model.isGroup,
                    model.isReadOnly.value == true,
                    (model.unreadMessageCount.value ?: 0) > 0,
                    {
                        adapter.resetSelection()
                    },
                    {
                        Log.i("$TAG Marking conversation [${model.id}] as read")
                        model.markAsRead()
                    },
                    {
                        Log.i("$TAG Changing mute status of conversation [${model.id}]")
                        model.toggleMute()
                    },
                    {
                        Log.i("$TAG Calling conversation [${model.id}]")
                        model.call()
                    },
                    {
                        showDeleteConfirmationDialog(model)
                    },
                    {
                        showLeaveConfirmationDialog(model)
                    }
                )

                modalBottomSheet.show(parentFragmentManager, ConversationDialogFragment.TAG)
                bottomSheetDialog = modalBottomSheet
            }
        }

        /*
         * Important:
         * Old working flow is kept.
         * Item click only sets selected chat room and triggers shared event.
         * Actual navigation happens inside sharedViewModel.showConversationEvent observer.
         */
        adapter.conversationClickedEvent.observe(viewLifecycleOwner) {
            it.consume { model ->
                Log.i("$TAG Chat item clicked with ID [${model.id}]")

                sharedViewModel.displayedChatRoom = model.chatRoom
                sharedViewModel.showConversationEvent.value = Event(model.id)
            }
        }

        adapter.createConversationWithFriendClickedEvent.observe(viewLifecycleOwner) {
            it.consume { friend ->
                coreContext.postOnCoreThread {
                    val singleAvailableAddress =
                        LinphoneUtils.getSingleAvailableAddressForFriend(friend)

                    if (singleAvailableAddress != null) {
                        Log.i(
                            "$TAG Only 1 SIP address or phone number found for contact [${friend.name}], using it"
                        )
                        listViewModel.createOneToOneChatRoomWith(singleAvailableAddress)
                    } else {
                        val list =
                            friend.getListOfSipAddressesAndPhoneNumbers(numberOrAddressClickListener)

                        Log.i(
                            "$TAG [${list.size}] numbers or addresses found for contact [${friend.name}], showing selection dialog"
                        )

                        coreContext.postOnMainThread {
                            showNumbersOrAddressesDialog(list)
                        }
                    }
                }
            }
        }

        adapter.createConversationWithAddressClickedEvent.observe(viewLifecycleOwner) {
            it.consume { address ->
                Log.i("$TAG Creating 1-1 conversation with to [${address.asStringUriOnly()}]")
                listViewModel.createOneToOneChatRoomWith(address)
            }
        }

        binding.setOnNewConversationClicked {
            if (findNavController().currentDestination?.id == R.id.conversationsListFragment) {
                Log.i("$TAG Navigating to start conversation fragment")

                val action =
                    ConversationsListFragmentDirections
                        .actionConversationsListFragmentToStartConversationFragment()

                findNavController().navigate(action)
            }
        }

        listViewModel.conversations.observe(viewLifecycleOwner) { list ->
            val filteredList = when (chatListMode) {
                CHAT_MODE_ONE_TO_ONE -> {
                    Log.i("$TAG Filtering only one-to-one conversations")

                    list.filter { wrapperModel ->
                        wrapperModel.conversationModel?.isGroup == false
                    }
                }

                CHAT_MODE_GROUP -> {
                    Log.i("$TAG Filtering only group conversations")

                    list.filter { wrapperModel ->
                        wrapperModel.conversationModel?.isGroup == true
                    }
                }

                else -> {
                    Log.i("$TAG Showing all conversations")
                    list
                }
            }

            adapter.submitList(filteredList)

            if (binding.conversationsList.adapter != adapter) {
                binding.conversationsList.adapter = adapter
            }

            Log.i(
                "$TAG Conversations list ready with original size [${list.size}] and filtered size [${filteredList.size}]"
            )

            listViewModel.fetchInProgress.value = false
        }

        /*
         * Important:
         * ConversationFragment is inside chat_nav_container child NavHost.
         * So use binding.chatNavContainer.findNavController(), not parent findNavController().
         * After navigate, open SlidingPaneLayout manually because initViews() is not used in custom layout.
         */
        listViewModel.chatRoomCreatedEvent.observe(viewLifecycleOwner) {
            it.consume { conversationId ->
                Log.i("$TAG Conversation [$conversationId] has been created, navigating to it")
                openConversation(conversationId)
            }
        }

        binding.setOnBackClicked {
            if (binding.slidingPaneLayout.isOpen) {
                binding.slidingPaneLayout.closePane()
            } else {
                findNavController().popBackStack()
            }
        }

        sharedViewModel.showConversationEvent.observe(viewLifecycleOwner) {
            it.consume { conversationId ->
                Log.i("$TAG Navigating to ConversationFragment with ID [$conversationId]")
                openConversation(conversationId)
            }
        }

        sharedViewModel.goToMeetingWaitingRoomEvent.observe(viewLifecycleOwner) {
            it.consume { uri ->
                if (findNavController().currentDestination?.id == R.id.conversationsListFragment) {
                    Log.i("$TAG Navigating to meeting waiting room fragment with URI [$uri]")

                    val action =
                        ConversationsListFragmentDirections
                            .actionConversationsListFragmentToMeetingWaitingRoomFragment(uri)

                    findNavController().navigate(action)
                } else {
                    Log.e(
                        "$TAG Failed to navigate to meeting waiting room, wrong current destination"
                    )
                }
            }
        }

        sharedViewModel.goToAccountProfileEvent.observe(viewLifecycleOwner) {
            it.consume {
                if (findNavController().currentDestination?.id == R.id.conversationsListFragment) {
                    val identity =
                        LinphoneUtils
                            .getDefaultAccount()
                            ?.params
                            ?.identityAddress
                            ?.asStringUriOnly()
                            .orEmpty()

                    val action =
                        ConversationsListFragmentDirections
                            .actionConversationsListFragmentToAccountProfileFragment(identity)

                    findNavController().navigate(action)
                }
            }
        }

        sharedViewModel.displayFileEvent.observe(viewLifecycleOwner) {
            it.consume { bundle ->
                if (findNavController().currentDestination?.id == R.id.conversationsListFragment) {
                    val path = bundle.getString("path", "")
                    val isMedia = bundle.getBoolean("isMedia", false)

                    if (path.isEmpty()) {
                        Log.e("$TAG Can't navigate to file viewer for empty path!")
                        return@consume
                    }

                    Log.i(
                        "$TAG Navigating to [${if (isMedia) "media" else "file"}] viewer with path [$path]"
                    )

                    if (isMedia) {
                        val intent = Intent(requireActivity(), MediaViewerActivity::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    } else {
                        val intent = Intent(requireActivity(), FileViewerActivity::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }
            }
        }

        sharedViewModel.updateConversationLastMessageEvent.observe(viewLifecycleOwner) {
            it.consume { conversationId ->
                val model = listViewModel.conversations.value.orEmpty().find { wrapperModel ->
                    wrapperModel.conversationModel?.id == conversationId
                }

                model?.conversationModel?.updateLastMessageInfo()
            }
        }

        sharedViewModel.forceRefreshDisplayedConversationEvent.observe(viewLifecycleOwner) {
            it.consume {
                val displayChatRoom = sharedViewModel.displayedChatRoom

                if (displayChatRoom != null) {
                    val found = listViewModel.conversations.value.orEmpty().find { wrapperModel ->
                        wrapperModel.conversationModel?.chatRoom == displayChatRoom
                    }

                    found?.conversationModel?.updateMuteState()
                }
            }
        }

        sharedViewModel.updateUnreadMessageCountForCurrentConversationEvent.observe(
            viewLifecycleOwner
        ) {
            it.consume {
                val displayChatRoom = sharedViewModel.displayedChatRoom

                if (displayChatRoom != null) {
                    val found = listViewModel.conversations.value.orEmpty().find { model ->
                        model.conversationModel?.chatRoom == displayChatRoom
                    }

                    found?.conversationModel?.updateUnreadCount()
                }

                listViewModel.updateUnreadMessagesCount()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.slidingPaneLayout.isOpen) {
                        binding.slidingPaneLayout.closePane()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
        )

        listViewModel.title.value = when (chatListMode) {
            CHAT_MODE_ONE_TO_ONE -> "Chat"
            CHAT_MODE_GROUP -> "Group Chat"
            else -> getString(R.string.bottom_navigation_conversations_label)
        }

        setViewModel(listViewModel)

        /*
         * Do not call initViews() here because your custom XML does not use default topBar/bottomNavBar.
         *
         * Old code used:
         * initViews(binding.slidingPaneLayout, binding.topBar, binding.bottomNavBar, R.id.conversationsListFragment)
         *
         * Now we manually open sliding pane inside openConversation().
         */

        val args = arguments
        if (args != null) {
            val conversationId = args.getString(ARGUMENTS_CONVERSATION_ID)

            if (!conversationId.isNullOrEmpty()) {
                Log.i("$TAG Found conversation ID [$conversationId] in arguments")

                sharedViewModel.showConversationEvent.value = Event(conversationId)
                args.clear()
            }
        }
    }

    /*
     * This is the most important fix.
     * It opens ConversationFragment inside chat_nav_container and then opens the right pane.
     */
    private fun openConversation(conversationId: String) {
        try {
            val action =
                ConversationFragmentDirections.actionGlobalConversationFragment(conversationId)

            val chatNavController = binding.chatNavContainer.findNavController()

            Log.i(
                "$TAG Opening ConversationFragment using chatNavContainer NavController, conversationId=[$conversationId]"
            )

            chatNavController.navigate(action)

            if (!binding.slidingPaneLayout.isOpen) {
                Log.i("$TAG Opening sliding pane to show conversation messages")
                binding.slidingPaneLayout.openPane()
            }
        } catch (e: IllegalArgumentException) {
            Log.e("$TAG Failed to open ConversationFragment: $e")
        } catch (e: IllegalStateException) {
            Log.e("$TAG Failed to open sliding pane / chat nav controller: $e")
        }
    }

    override fun onResume() {
        super.onResume()

        if (listViewModel.hideConversations.value == true) {
            Log.w(
                "$TAG Resuming fragment that should no longer be accessible, going to contacts list instead"
            )
            sharedViewModel.navigateToContactsEvent.value = Event(true)
        }

        try {
            adapter.registerAdapterDataObserver(dataObserver)
        } catch (e: IllegalStateException) {
            Log.e("$TAG Failed to register data observer to adapter: $e")
        }

        if (shouldRefreshDataInOnResume()) {
            Log.i("$TAG Keep app alive setting is enabled, refreshing view just in case")
            listViewModel.filter()
        }
    }

    override fun onPause() {
        super.onPause()

        bottomSheetDialog?.dismiss()
        bottomSheetDialog = null

        try {
            adapter.unregisterAdapterDataObserver(dataObserver)
        } catch (e: IllegalStateException) {
            Log.e("$TAG Failed to unregister data observer to adapter: $e")
        }
    }

    private fun showNumbersOrAddressesDialog(list: List<ContactNumberOrAddressModel>) {
        val numberOrAddressModel = NumberOrAddressPickerDialogModel(list)

        val dialog =
            DialogUtils.getNumberOrAddressPickerDialog(
                requireActivity(),
                numberOrAddressModel
            )

        numberOrAddressModel.dismissEvent.observe(viewLifecycleOwner) { event ->
            event.consume {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(conversationModel: ConversationModel) {
        val dialogModel = ConfirmationDialogModel()

        val dialog =
            DialogUtils.getDeleteConversationConfirmationDialog(
                requireActivity(),
                dialogModel
            )

        dialogModel.dismissEvent.observe(viewLifecycleOwner) {
            it.consume {
                dialog.dismiss()
            }
        }

        dialogModel.confirmEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Deleting conversation [${conversationModel.id}]")
                conversationModel.delete()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showLeaveConfirmationDialog(conversationModel: ConversationModel) {
        val dialogModel = ConfirmationDialogModel()

        val dialog =
            DialogUtils.getLeaveConversationConfirmationDialog(
                requireActivity(),
                dialogModel
            )

        dialogModel.dismissEvent.observe(viewLifecycleOwner) {
            it.consume {
                dialog.dismiss()
            }
        }

        dialogModel.confirmEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Leaving group conversation [${conversationModel.id}]")
                conversationModel.leaveGroup()
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}
