package org.linphone.ui.more.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.linphone.LinphoneApplication.Companion.coreContext
import org.linphone.R
import org.linphone.core.tools.Log
import org.linphone.databinding.MoreFragmentBinding
import org.linphone.ui.main.fragment.AbstractMainFragment
import org.linphone.ui.main.more.viewmodel.MoreViewModel
import org.linphone.ui.main.more.viewmodel.MoreViewModel.Companion.OPTION_ABOUT
import org.linphone.ui.main.more.viewmodel.MoreViewModel.Companion.OPTION_HELP
// import org.linphone.ui.main.more.viewmodel.MoreViewModel.Companion.OPTION_LOGOUT
import org.linphone.ui.main.more.viewmodel.MoreViewModel.Companion.OPTION_PRIVACY
import org.linphone.ui.main.more.viewmodel.MoreViewModel.Companion.OPTION_PROFILE
import org.linphone.ui.main.more.viewmodel.MoreViewModel.Companion.OPTION_SETTINGS
import org.linphone.ui.more.adapters.MoreOptionsAdapter

class MoreFragment : AbstractMainFragment() {
    companion object {
        private const val TAG = "[More Fragment]"
    }

    private lateinit var binding: MoreFragmentBinding

    private lateinit var viewModel: MoreViewModel

    private lateinit var adapter: MoreOptionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = MoreOptionsAdapter { option ->
            viewModel.onOptionClicked(option.id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MoreFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDefaultAccountChanged() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MoreViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.moreOptionsList.setHasFixedSize(true)
        binding.moreOptionsList.layoutManager = LinearLayoutManager(requireContext())
        binding.moreOptionsList.adapter = adapter

        binding.setOnBackClicked {
            handleBackPress()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress()
                }
            }
        )

        viewModel.options.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.optionClickedEvent.observe(viewLifecycleOwner) {
            it.consume { optionId ->
                handleOptionClick(optionId)
            }
        }

        setViewModel(viewModel)

        /*
         * Do not call initViews().
         * This keeps More screen without default top bar,
         * bottom navigation, and drawer / side menu.
         */
    }

    private fun handleBackPress() {
        findNavController().popBackStack()
    }

    private fun handleOptionClick(optionId: Int) {
        when (optionId) {
            OPTION_PROFILE -> {
                Log.i("$TAG Profile clicked")

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

            }

            OPTION_SETTINGS -> {
                Log.i("$TAG Settings clicked")

                val navOptions = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build()

                findNavController().navigate(
                    R.id.action_global_settingsFragment,
                    null,
                    navOptions
                )
            }

            OPTION_PRIVACY -> {
                Log.i("$TAG Privacy clicked")
                openUrl("https://system.swisspack.us/privacy-policy")
            }

            OPTION_HELP -> {
                Log.i("$TAG Help clicked")
                openUrl("https://system.swisspack.us/help-support")
            }

            OPTION_ABOUT -> {
                Log.i("$TAG About clicked")
                openUrl("https://system.swisspack.us/about")
            }

//            OPTION_LOGOUT -> {
//                Log.i("$TAG Logout clicked")
//
//            }
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("$TAG Failed to open URL [$url]: $e")
        }
    }
}
