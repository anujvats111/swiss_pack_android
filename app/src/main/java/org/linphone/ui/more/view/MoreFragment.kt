package org.linphone.ui.more.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.linphone.core.tools.Log
import org.linphone.databinding.MoreFragmentBinding
import org.linphone.ui.main.fragment.AbstractMainFragment
import org.linphone.ui.main.more.viewmodel.MoreViewModel
import org.linphone.ui.main.more.viewmodel.MoreViewModel.Companion.OPTION_ABOUT
import org.linphone.ui.main.more.viewmodel.MoreViewModel.Companion.OPTION_HELP
import org.linphone.ui.main.more.viewmodel.MoreViewModel.Companion.OPTION_LOGOUT
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
                // findNavController().navigate(R.id.action_moreFragment_to_accountProfileFragment)
            }

            OPTION_SETTINGS -> {
                Log.i("$TAG Settings clicked")
                // findNavController().navigate(R.id.action_moreFragment_to_settingsFragment)
            }

            OPTION_PRIVACY -> {
                Log.i("$TAG Privacy clicked")
                openUrl("https://www.swisspack.com/privacy-policy")
            }

            OPTION_HELP -> {
                Log.i("$TAG Help clicked")
                // findNavController().navigate(R.id.action_moreFragment_to_helpFragment)
            }

            OPTION_ABOUT -> {
                Log.i("$TAG About clicked")
                // findNavController().navigate(R.id.action_moreFragment_to_aboutFragment)
            }

            OPTION_LOGOUT -> {
                Log.i("$TAG Logout clicked")
                // Add your logout logic here
            }
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
