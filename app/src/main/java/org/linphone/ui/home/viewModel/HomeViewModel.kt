package org.linphone.ui.home.viewModel

import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.linphone.utils.Event

class HomeViewModel
@UiThread
constructor() : ViewModel() {

    val openDrawerMenuEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToFriendsEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToGroupsEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToMoreEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToChatEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToPhoneEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToVideoCallEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToGroupChatEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToAudioMeetingEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToVideoMeetingEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToBillboardEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToBroadcastEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToArtificialIntelligenceEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToCalendarEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToNotesEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    val navigateToInviteEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData()
    }

    @UiThread
    fun openDrawerMenu() {
        openDrawerMenuEvent.value = Event(true)
    }

    @UiThread
    fun navigateToFriends() {
        navigateToFriendsEvent.value = Event(true)
    }

    @UiThread
    fun navigateToGroups() {
        navigateToGroupsEvent.value = Event(true)
    }

   @UiThread
    fun navigateToMore() {
       navigateToMoreEvent.value = Event(true)
    }

    @UiThread
    fun navigateToChat() {
        navigateToChatEvent.value = Event(true)
    }

    @UiThread
    fun navigateToPhone() {
        navigateToPhoneEvent.value = Event(true)
    }

    @UiThread
    fun navigateToVideoCall() {
        navigateToVideoCallEvent.value = Event(true)
    }

    @UiThread
    fun navigateToGroupChat() {
        navigateToGroupChatEvent.value = Event(true)
    }

    @UiThread
    fun navigateToAudioMeeting() {
        navigateToAudioMeetingEvent.value = Event(true)
    }

    @UiThread
    fun navigateToVideoMeeting() {
        navigateToVideoMeetingEvent.value = Event(true)
    }

    @UiThread
    fun navigateToBillboard() {
        navigateToBillboardEvent.value = Event(true)
    }

    @UiThread
    fun navigateToBroadcast() {
        navigateToBroadcastEvent.value = Event(true)
    }

    @UiThread
    fun navigateToArtificialIntelligence() {
        navigateToArtificialIntelligenceEvent.value = Event(true)
    }

    @UiThread
    fun navigateToCalendar() {
        navigateToCalendarEvent.value = Event(true)
    }

    @UiThread
    fun navigateToNotes() {
        navigateToNotesEvent.value = Event(true)
    }

    @UiThread
    fun navigateToInvite() {
        navigateToInviteEvent.value = Event(true)
    }
}
