package org.linphone.ui.more

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.linphone.R
import org.linphone.databinding.HomeMenuFragmentBinding

class HomeMenuFragment : Fragment(R.layout.home_menu_fragment) {

    private var _binding: HomeMenuFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = HomeMenuFragmentBinding.bind(view)

        binding.menuFriends.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_contactsListFragment)
        }

        binding.menuGroups.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_contactsListFragment)
        }

        binding.menuChat.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_conversationsListFragment)
        }

        binding.menuPhone.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_historyListFragment)
        }

        binding.menuVideoCall.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_historyListFragment)
        }

        binding.menuGroupChat.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_conversationsListFragment)
        }

        binding.menuAudioMeeting.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_meetingsListFragment)
        }

        binding.menuVideoMeeting.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_meetingsListFragment)
        }

        binding.menuBillboard.setOnClickListener {
            // TODO: open Billboard screen
        }

        binding.menuBroadcast.setOnClickListener {
            // TODO: open Broadcast screen
        }

        binding.menuAi.setOnClickListener {
            // TODO: open Artificial Intelligence screen
        }

        binding.menuCalendar.setOnClickListener {
            // TODO: open Calendar screen
        }

        binding.menuNotes.setOnClickListener {
            // TODO: open Notes screen
        }

        binding.bottomInvite.setOnClickListener {
            // TODO: open invite screen
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
