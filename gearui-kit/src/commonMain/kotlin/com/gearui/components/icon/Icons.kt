package com.gearui.components.icon

/**
 * Icons registry (aligned with internal icon style).
 *
 * Usage:
 * - `Icons.home` -> icon name
 * - `Icons.png(Icons.home)` -> assets://icons/home.png
 * - `Icons.svg(Icons.home)` -> assets://icons/home.svg
 */
object Icons {
    const val account_circle = "account_circle"
    const val add = "add"
    const val alternate_email = "alternate_email"
    const val attach_file = "attach_file"
    const val arrow_back = "arrow_back"
    const val arrow_forward = "arrow_forward"
    const val autorenew = "autorenew"
    const val bookmark_border = "bookmark_border"
    const val bookmark = "bookmark"
    const val call_end = "call_end"
    const val call_made = "call_made"
    const val call_received = "call_received"
    const val call = "call"
    const val camera_alt = "camera_alt"
    const val chat = "chat"
    const val check_box_outline_blank = "check_box_outline_blank"
    const val check_box = "check_box"
    const val check = "check"
    const val chevron_left = "chevron_left"
    const val chevron_right = "chevron_right"
    const val close = "close"
    const val contacts = "contacts"
    const val content_copy = "content_copy"
    const val delete = "delete"
    const val done = "done"
    const val edit = "edit"
    const val error = "error"
    const val event = "event"
    const val favorite_border = "favorite_border"
    const val favorite = "favorite"
    const val file_download = "file_download"
    const val file_upload = "file_upload"
    const val flag = "flag"
    const val forward = "forward"
    const val forum = "forum"
    const val group = "group"
    const val groups = "groups"
    const val history = "history"
    const val home = "home"
    const val hourglass_empty = "hourglass_empty"
    const val image = "image"
    const val indeterminate_check_box = "indeterminate_check_box"
    const val info = "info"
    const val keyboard_arrow_down = "keyboard_arrow_down"
    const val keyboard_arrow_up = "keyboard_arrow_up"
    const val link_off = "link_off"
    const val link = "link"
    const val mail = "mail"
    const val menu = "menu"
    const val mic_off = "mic_off"
    const val mic = "mic"
    const val more_time = "more_time"
    const val more_horiz = "more_horiz"
    const val more_vert = "more_vert"
    const val no_photography = "no_photography"
    const val notifications_active = "notifications_active"
    const val notifications_none = "notifications_none"
    const val notifications_off = "notifications_off"
    const val notifications = "notifications"
    const val open_in_new = "open_in_new"
    const val pause = "pause"
    const val pending = "pending"
    const val person_add = "person_add"
    const val person_remove = "person_remove"
    const val person = "person"
    const val photo_camera = "photo_camera"
    const val play_arrow = "play_arrow"
    const val radio_button_checked = "radio_button_checked"
    const val radio_button_unchecked = "radio_button_unchecked"
    const val refresh = "refresh"
    const val remove = "remove"
    const val report = "report"
    const val reply = "reply"
    const val schedule = "schedule"
    const val search = "search"
    const val send = "send"
    const val send_time_extension = "send_time_extension"
    const val settings = "settings"
    const val share = "share"
    const val share_off = "share_off"
    const val star_border = "star_border"
    const val star_rate = "star_rate"
    const val star_half = "star_half"
    const val star_filled = "star_filled"
    const val star = "star"
    const val sync_alt = "sync_alt"
    const val sync = "sync"
    const val thumb_up_off_alt = "thumb_up_off_alt"
    const val thumb_up = "thumb_up"
    const val timer = "timer"
    const val translate = "translate"
    const val tune = "tune"
    const val update = "update"
    const val videocam_off = "videocam_off"
    const val videocam = "videocam"
    const val warning = "warning"
    const val upload = "upload"
    const val download = "download"
    const val launch = "launch"
    const val logout = "logout"

    val all = listOf(
        account_circle,
        add,
        alternate_email,
        attach_file,
        arrow_back,
        arrow_forward,
        autorenew,
        bookmark_border,
        bookmark,
        call_end,
        call_made,
        call_received,
        call,
        camera_alt,
        chat,
        check_box_outline_blank,
        check_box,
        check,
        chevron_left,
        chevron_right,
        close,
        contacts,
        content_copy,
        delete,
        done,
        edit,
        error,
        event,
        favorite_border,
        favorite,
        file_download,
        file_upload,
        flag,
        forward,
        forum,
        group,
        groups,
        history,
        home,
        hourglass_empty,
        image,
        indeterminate_check_box,
        info,
        keyboard_arrow_down,
        keyboard_arrow_up,
        link_off,
        link,
        mail,
        menu,
        mic_off,
        mic,
        more_time,
        more_horiz,
        more_vert,
        no_photography,
        notifications_active,
        notifications_none,
        notifications_off,
        notifications,
        open_in_new,
        pause,
        pending,
        person_add,
        person_remove,
        person,
        photo_camera,
        play_arrow,
        radio_button_checked,
        radio_button_unchecked,
        refresh,
        remove,
        report,
        reply,
        schedule,
        search,
        send,
        send_time_extension,
        settings,
        share,
        share_off,
        star_border,
        star_rate,
        star_half,
        star_filled,
        star,
        sync_alt,
        sync,
        thumb_up_off_alt,
        thumb_up,
        timer,
        translate,
        tune,
        update,
        upload,
        download,
        videocam_off,
        videocam,
        warning,
        launch,
        logout,
    )

    fun png(name: String): String = "assets://icons/$name.png"

    fun svg(name: String): String = "assets://icons/$name.svg"
}
