package com.gearui.components.icon

/**
 * The GearUI icon set — [Phosphor](https://phosphoricons.com), regular weight.
 *
 * Names are Phosphor's own, with dashes as underscores: `Icons.house`,
 * `Icons.magnifying_glass`, `Icons.x`. They used to be Material Symbols names
 * over Phosphor artwork, so `Icons.house` fetched `house` and `Icons.x`
 * fetched `x` — the set was described in a vocabulary it did not use, and the
 * two halves drifted until the SVGs were a different icon library from the PNGs.
 *
 * Regular weight throughout, except three `_fill` variants that carry state:
 * [star_fill] against [star] for Rate, [bookmark_simple_fill] against
 * [bookmark_simple] for pin and unpin, and [play_fill] because a solid triangle
 * is the play affordance.
 *
 * Usage:
 * - `Icons.house` -> icon name
 * - `Icons.png(Icons.house)` -> assets://icons/house.png
 *
 * The set is generated: `scripts/icon-set.py` lists it and
 * `scripts/gen_icons.py` renders the assets. An **application** adds its own
 * icons by dropping PNGs into its own `assets/icons/`, which needs nothing from
 * here — see DESIGN_SYSTEM_SPEC 11.4.
 */
object Icons {
    const val address_book = "address_book"
    const val arrow_bend_up_left = "arrow_bend_up_left"
    const val arrow_bend_up_right = "arrow_bend_up_right"
    const val arrow_clockwise = "arrow_clockwise"
    const val arrow_down_left = "arrow_down_left"
    const val arrow_left = "arrow_left"
    const val arrow_right = "arrow_right"
    const val arrow_square_out = "arrow_square_out"
    const val arrow_up_right = "arrow_up_right"
    const val arrows_clockwise = "arrows_clockwise"
    const val arrows_left_right = "arrows_left_right"
    const val at = "at"
    const val bell = "bell"
    const val bell_ringing = "bell_ringing"
    const val bell_slash = "bell_slash"
    const val bookmark_simple = "bookmark_simple"
    const val bookmark_simple_fill = "bookmark_simple_fill"
    const val calendar_blank = "calendar_blank"
    const val camera = "camera"
    const val camera_slash = "camera_slash"
    const val caret_down = "caret_down"
    const val caret_left = "caret_left"
    const val caret_right = "caret_right"
    const val caret_up = "caret_up"
    const val chat_circle = "chat_circle"
    const val chats = "chats"
    const val check = "check"
    const val check_square = "check_square"
    const val circle = "circle"
    const val clock = "clock"
    const val clock_clockwise = "clock_clockwise"
    const val clock_counter_clockwise = "clock_counter_clockwise"
    const val copy = "copy"
    const val dots_three = "dots_three"
    const val dots_three_circle = "dots_three_circle"
    const val dots_three_vertical = "dots_three_vertical"
    const val download_simple = "download_simple"
    const val envelope_simple = "envelope_simple"
    const val faders = "faders"
    const val flag = "flag"
    const val gear = "gear"
    const val gift = "gift"
    const val heart = "heart"
    const val hourglass = "hourglass"
    const val house = "house"
    const val image = "image"
    const val info = "info"
    const val link = "link"
    const val link_break = "link_break"
    const val list = "list"
    const val lock_simple = "lock_simple"
    const val magnifying_glass = "magnifying_glass"
    const val microphone = "microphone"
    const val microphone_slash = "microphone_slash"
    const val minus = "minus"
    const val minus_square = "minus_square"
    const val paper_plane_right = "paper_plane_right"
    const val paper_plane_tilt = "paper_plane_tilt"
    const val paperclip = "paperclip"
    const val pause = "pause"
    const val pencil_simple = "pencil_simple"
    const val phone = "phone"
    const val phone_slash = "phone_slash"
    const val play_fill = "play_fill"
    const val plus = "plus"
    const val prohibit = "prohibit"
    const val qr_code = "qr_code"
    const val question = "question"
    const val radio_button = "radio_button"
    const val share_network = "share_network"
    const val shield_check = "shield_check"
    const val sign_out = "sign_out"
    const val square = "square"
    const val star = "star"
    const val star_fill = "star_fill"
    const val star_half = "star_half"
    const val thumbs_up = "thumbs_up"
    const val timer = "timer"
    const val translate = "translate"
    const val trash = "trash"
    const val upload_simple = "upload_simple"
    const val user = "user"
    const val user_circle = "user_circle"
    const val user_minus = "user_minus"
    const val user_plus = "user_plus"
    const val users = "users"
    const val users_three = "users_three"
    const val video_camera = "video_camera"
    const val video_camera_slash = "video_camera_slash"
    const val wallet = "wallet"
    const val warning = "warning"
    const val warning_circle = "warning_circle"
    const val warning_octagon = "warning_octagon"
    const val x = "x"

    /** Every icon name, for the gallery and for tooling. */
    val all = listOf(
        address_book,
        arrow_bend_up_left,
        arrow_bend_up_right,
        arrow_clockwise,
        arrow_down_left,
        arrow_left,
        arrow_right,
        arrow_square_out,
        arrow_up_right,
        arrows_clockwise,
        arrows_left_right,
        at,
        bell,
        bell_ringing,
        bell_slash,
        bookmark_simple,
        bookmark_simple_fill,
        calendar_blank,
        camera,
        camera_slash,
        caret_down,
        caret_left,
        caret_right,
        caret_up,
        chat_circle,
        chats,
        check,
        check_square,
        circle,
        clock,
        clock_clockwise,
        clock_counter_clockwise,
        copy,
        dots_three,
        dots_three_circle,
        dots_three_vertical,
        download_simple,
        envelope_simple,
        faders,
        flag,
        gear,
        gift,
        heart,
        hourglass,
        house,
        image,
        info,
        link,
        link_break,
        list,
        lock_simple,
        magnifying_glass,
        microphone,
        microphone_slash,
        minus,
        minus_square,
        paper_plane_right,
        paper_plane_tilt,
        paperclip,
        pause,
        pencil_simple,
        phone,
        phone_slash,
        play_fill,
        plus,
        prohibit,
        qr_code,
        question,
        radio_button,
        share_network,
        shield_check,
        sign_out,
        square,
        star,
        star_fill,
        star_half,
        thumbs_up,
        timer,
        translate,
        trash,
        upload_simple,
        user,
        user_circle,
        user_minus,
        user_plus,
        users,
        users_three,
        video_camera,
        video_camera_slash,
        wallet,
        warning,
        warning_circle,
        warning_octagon,
        x,
    )

    /**
     * Asset URL for an icon.
     *
     * PNG only. There used to be an SVG path behind `Icon(preferSvg = true)`
     * and it never worked: Kuikly hands the URL to the platform image loader,
     * and neither Android's `BitmapFactory` nor iOS's
     * `UIImage imageWithContentsOfFile:` decodes SVG. Only the web renderer
     * does.
     */
    fun png(name: String): String = "assets://icons/$name.png"
}
