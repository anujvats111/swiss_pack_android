package org.linphone.ui.main.more.viewmodel

import androidx.lifecycle.MutableLiveData
import org.linphone.R
import org.linphone.ui.main.viewmodel.AbstractMainViewModel
import org.linphone.ui.more.model.MoreOptionModel
import org.linphone.utils.Event

class MoreViewModel : AbstractMainViewModel() {
    companion object {
        const val OPTION_PROFILE = 1
        const val OPTION_SETTINGS = 2
        const val OPTION_PRIVACY = 3
        const val OPTION_HELP = 4
        const val OPTION_ABOUT = 5
    //        const val OPTION_LOGOUT = 6
    }

    val options = MutableLiveData<List<MoreOptionModel>>()

    val optionClickedEvent: MutableLiveData<Event<Int>> by lazy {
        MutableLiveData()
    }

    init {
        title.value = "More"

        options.value = listOf(
            MoreOptionModel(
                OPTION_PROFILE,
                "Profile",
                R.drawable.user_circle
            ),
            MoreOptionModel(
                OPTION_SETTINGS,
                "Settings",
                R.drawable.gear
            ),
            MoreOptionModel(
                OPTION_PRIVACY,
                "Privacy Policy",
                R.drawable.shield_warning
            ),
            MoreOptionModel(
                OPTION_HELP,
                "Help & Support",
                R.drawable.question
            ),
            MoreOptionModel(
                OPTION_ABOUT,
                "About",
                R.drawable.info
            ),
//            MoreOptionModel(
//                OPTION_VERSION,
//                "Version",
//                R.drawable.version
//            ),
//            MoreOptionModel(
//                OPTION_LOGOUT,
//                "Logout",
//                R.drawable.sign_out
//            )
        )
    }

    fun onOptionClicked(optionId: Int) {
        optionClickedEvent.value = Event(optionId)
    }
}
