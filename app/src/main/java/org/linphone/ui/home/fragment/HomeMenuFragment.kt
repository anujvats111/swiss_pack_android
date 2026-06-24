package org.linphone.ui.home.fragment

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
                Toast.makeText(requireContext(), "Billboard will come soon", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.navigateToBroadcastEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Broadcast")
                Toast.makeText(requireContext(), "Broadcast will come soon", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.navigateToArtificialIntelligenceEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Artificial Intelligence")
                Toast.makeText(requireContext(), "Artificial Intelligence will come soon", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.navigateToCalendarEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Calendar")
                Toast.makeText(requireContext(), "Calendar will come soon", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.navigateToNotesEvent.observe(viewLifecycleOwner) {
            it.consume {
                Log.i("$TAG Opening Notes")
                Toast.makeText(requireContext(), "Notes will come soon", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
