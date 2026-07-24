package org.linphone.ui.home.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.linphone.R
import org.linphone.core.tools.Log
import org.linphone.databinding.HomeMenuFragmentBinding
import org.linphone.ui.home.viewModel.HomeViewModel
import org.linphone.ui.main.chat.fragment.ConversationsListFragment
import org.linphone.ui.main.history.fragment.HistoryListFragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.CalendarContract
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.navigation.NavOptions
import org.linphone.LinphoneApplication.Companion.coreContext

class HomeMenuFragment : Fragment(R.layout.home_menu_fragment) {

    companion object {
        private const val TAG = "[Home Menu Fragment]"
    }

    private var _binding: HomeMenuFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = HomeMenuFragmentBinding.bind(view)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observeViewModel()
    }

    private fun observeViewModel() {
//        viewModel.openDrawerMenuEvent.observe(viewLifecycleOwner) {
//            it.consume {
//                (requireActivity() as MainActivity).toggleDrawerMenu()
//            }
//        }

        viewModel.navigateToMenuEvent.observe(viewLifecycleOwner) {
            it.consume {
                showProfileMenuPopup()
            }
        }

        viewModel.navigateToMoreEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Groups")
                findNavController().navigate(R.id.action_homeMenuFragment_to_moreFragment)
            }
        }

        viewModel.navigateToFriendsEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Friends")
                findNavController().navigate(R.id.action_homeMenuFragment_to_contactsListFragment)
            }
        }

        viewModel.navigateToGroupsEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Groups")
                findNavController().navigate(
                    R.id.action_homeMenuFragment_to_conversationsListFragment,
                    bundleOf(
                        ConversationsListFragment.ARG_CHAT_LIST_MODE to ConversationsListFragment.CHAT_MODE_GROUP
                    )
                )
            }
        }

        viewModel.navigateToChatEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening One to One Chat")

                findNavController().navigate(
                    R.id.action_homeMenuFragment_to_conversationsListFragment,
                    bundleOf(
                        ConversationsListFragment.ARG_CHAT_LIST_MODE to ConversationsListFragment.CHAT_MODE_ONE_TO_ONE
                    )
                )
            }
        }

//        viewModel.navigateToChatEvent.observe(viewLifecycleOwner) {
//            it.consume {
//                Log.i("$TAG Opening Chat")
//                findNavController().navigate(R.id.action_homeMenuFragment_to_conversationsListFragment)
//            }
//        }

        viewModel.navigateToPhoneEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Phone")

                val bundle = Bundle().apply {
                    putInt(
                        HistoryListFragment.ARG_HISTORY_LIST_MODE,
                        HistoryListFragment.HISTORY_MODE_AUDIO
                    )
                }

                findNavController().navigate(
                    R.id.action_homeMenuFragment_to_historyListFragment,
                    bundle
                )
            }
        }

        viewModel.navigateToVideoCallEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Video Call")

                val bundle = Bundle().apply {
                    putInt(
                        HistoryListFragment.ARG_HISTORY_LIST_MODE,
                        HistoryListFragment.HISTORY_MODE_VIDEO
                    )
                }

                findNavController().navigate(
                    R.id.action_homeMenuFragment_to_historyListFragment,
                    bundle
                )
            }
        }

//        viewModel.navigateToGroupChatEvent.observe(viewLifecycleOwner) {
//            it.consume {
//                Log.i("$TAG Opening Group Chat")
//                findNavController().navigate(R.id.action_homeMenuFragment_to_conversationsListFragment)
//            }
//        }
        viewModel.navigateToGroupChatEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Group Chat")

                findNavController().navigate(
                    R.id.action_homeMenuFragment_to_conversationsListFragment,
                    bundleOf(
                        ConversationsListFragment.ARG_CHAT_LIST_MODE to ConversationsListFragment.CHAT_MODE_GROUP
                    )
                )
            }
        }

        viewModel.navigateToAudioMeetingEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Audio Meeting")
                findNavController().navigate(R.id.action_homeMenuFragment_to_meetingsListFragment)
            }
        }

        viewModel.navigateToVideoMeetingEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Video Meeting")
                findNavController().navigate(R.id.action_homeMenuFragment_to_meetingsListFragment)
            }
        }

        viewModel.navigateToBillboardEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Billboard")
            }
        }

        viewModel.navigateToBroadcastEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Broadcast")
            }
        }

        viewModel.navigateToArtificialIntelligenceEvent.observe(viewLifecycleOwner) {
            it.consume {
                openAvailableAiApp()
            }
        }

        viewModel.navigateToCalendarEvent.observe(viewLifecycleOwner) {
            it.consume {
                openCalendarApp()
            }
        }

        viewModel.navigateToNotesEvent.observe(viewLifecycleOwner) {
            it.consume {
                openNotebookApp()
            }
        }

        viewModel.navigateToInviteEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Invite")

                val inviteMessage = """
            Join me on Swisspack for quick and easy conferencing and communication!

            Download now:

            Android:
            https://play.google.com/store/apps/details?id=com.swisspack

            Apple:
            https://apps.apple.com/
        """.trimIndent()

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Join me on Swisspack")
                    putExtra(Intent.EXTRA_TEXT, inviteMessage)
                }

                try {
                    startActivity(
                        Intent.createChooser(
                            shareIntent,
                            "Invite via"
                        )
                    )
                } catch (e: Exception) {
                    Log.e("$TAG Failed to open invite share sheet: $e")
                    Toast.makeText(
                        requireContext(),
                        "No app found to share invite",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openAvailableAiApp() {
        val aiApps = listOf(
            AppPackage("ChatGPT", "com.openai.chatgpt"),
            AppPackage("Gemini", "com.google.android.apps.bard"),
            AppPackage("Meta AI", "com.facebook.stella")
        )

        for (app in aiApps) {
            if (isPackageInstalled(requireContext(), app.packageName)) {
                openApp(requireContext(), app.packageName)
                return
            }
        }

        Toast.makeText(
            requireContext(),
            "No AI app found. Please install ChatGPT, Gemini, or Meta AI.",
            Toast.LENGTH_SHORT
        ).show()

//        openPlayStore(requireContext(), "com.openai.chatgpt")
    }

    @SuppressLint("MissingInflatedId")
    private fun showProfileMenuPopup() {
        val popupView = layoutInflater.inflate(R.layout.top_bar_menu_popup, null)

        val popupWindow = PopupWindow(
            popupView,
            resources.getDimensionPixelSize(R.dimen.top_bar_menu_width),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.elevation = 12f

        popupView.findViewById<View>(R.id.manage_account).setOnClickListener {
            popupWindow.dismiss()
            try {
                val account = coreContext.core.defaultAccount
                val identity = account?.params?.identityAddress?.asStringUriOnly().orEmpty()

                if (identity.isNotEmpty()) {
                    val args = bundleOf(
                        "accountIdentity" to identity
                    )

                    val navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                        .setPopEnterAnim(R.anim.slide_in_left)
                        .setPopExitAnim(R.anim.slide_out_right)
                        .build()

                    findNavController().navigate(
                        R.id.action_global_accountProfileFragment,
                        args,
                        navOptions
                    )
                } else {
                    Log.e("$TAG No default account identity found")
                }
//                findNavController().navigate(R.id.accountProfileFragment)
            } catch (e: Exception) {
                Log.e("$TAG Failed to navigate to profile menu: $e")
            }
        }

        popupView.findViewById<View>(R.id.settings).setOnClickListener {
            popupWindow.dismiss()
            try {
                findNavController().navigate(R.id.settingsFragment)
            } catch (e: Exception) {
                Log.e("$TAG Failed to navigate to profile menu: $e")
            }
        }

        val anchorView = requireView().findViewById<View>(R.id.menu_more)
        popupWindow.showAsDropDown(anchorView, -260, 0)
    }

    private fun openNotebookApp() {
        val packageManager = requireContext().packageManager

        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = packageManager.queryIntentActivities(launcherIntent, 0)

        val notesApp = apps.firstOrNull { resolveInfo ->
            val appName = resolveInfo.loadLabel(packageManager).toString().lowercase()
            val packageName = resolveInfo.activityInfo.packageName.lowercase()

            appName == "notes" ||
                    appName == "note" ||
                    appName.contains("notes") ||
                    appName.contains("note") ||
                    packageName.contains("notes") ||
                    packageName.contains("note")
        }

        if (notesApp != null) {
            val packageName = notesApp.activityInfo.packageName
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)

            if (launchIntent != null) {
                startActivity(launchIntent)
                return
            }
        }

        // Fallback: Google Keep
        openGoogleKeep()
    }

    private fun openGoogleKeep() {
        val googleKeepPackage = "com.google.android.keep"

        val launchIntent = requireContext()
            .packageManager
            .getLaunchIntentForPackage(googleKeepPackage)

        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            openPlayStore(requireContext(), googleKeepPackage)
        }
    }

    private fun openCalendarApp() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
            }
            startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = CalendarContract.CONTENT_URI.buildUpon()
                        .appendPath("time")
                        .build()
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "No calendar app found.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun openApp(context: Context, packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        } else {
            openPlayStore(context, packageName)
        }
    }

    private fun openPlayStore(context: Context, packageName: String) {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    data class AppPackage(
        val name: String,
        val packageName: String
    )

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
