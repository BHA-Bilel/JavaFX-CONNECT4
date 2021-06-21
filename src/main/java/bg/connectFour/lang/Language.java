package bg.connectFour.lang;

import bg.connectFour.MainApp;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Language {

    public static LANGNAME LANG_NAME;

    public static StringProperty
            COINCHE, DOMINOS, CONNECT4, XO, CHESS, CHECKERS,
            SKIP_H, SKIP_C, GO_BACK, SKIP_BT,
            LANG_H, LANG_C,
            THEME_H, THEME_C, LIGHT, DARK,
            WELCOME_H, WELCOME_C, WELCOME_BACK,
            FINAL_PHASE_H, FINAL_PHASE_C,
            HOLD_H, HOLD_C,
            ONLINE_SETUP_H, ONLINE_SETUP_C, ONLINE_SETUP_SUCCESS, ONLINE_SETUP_FAIL_H, ONLINE_SETUP_FAIL_C,
            LOCAL_SETUP_H, LOCAL_SETUP_C, LOCAL_SETUP_SUCCESS, LOCAL_SETUP_FAIL_H, LOCAL_SETUP_FAIL_C,
            MIGRATION_H1, MIGRATION_C1, MIGRATION_H2, MIGRATION_C2,
            LOCAL, ONLINE,
            RETRY, CANCEL, OK, ACCEPT, PREVIOUS, NEXT, DONE,
            GS_NA_C, COMM_ERROR_H, COMM_ERROR_C,
            CNT_ACCESS_H, CNT_ACCESS_C,
            CNT_SG_H, CNT_SG_C1, CNT_SG_C2,
            JOIN_ERROR, GONE_PRIVATE, NO_ROOMS, CHK_INTERNET, CHK_LOCAL,

    WINDOW, SWITCH_FS, SWITCH_THEME,

    SHORTCUTS, GENERAL, MAIN, JOIN, ROOM, GAME,

    GEN_SH_T, GEN_SH_H, GEN_SH_C,
            M_SH_T, M_SH_H, M_SH_C,
            J_SH_T, J_SH_H, J_SH_C,
            R_SH_T, R_SH_H1, R_SH_H2, R_SH_C1, R_SH_C2, OPEN_CH,
            G_SH_T, G_SH_H, G_SH_C,
            VIEW_SC, GR_H, DRAWS,
            END_GAME, GE_H, GE_C,

    NETWORK,
            CH_ON_IP, CH_ON_PORT, CH_ON_TO,
            CH_MC_IP, CH_JN_PORT, CH_HS_PORT, CH_LC_TO,
            CH_ON_IP_H, CH_MC_IP_H, CH_PORT_H, CH_TO_H,
            SAME_JH_H, SAME_JH_C1, SAME_JH_C2,
            INV_IP_H, INV_IP_C, INV_MC_IP_H, INV_MC_IP_C,
            INV_PORT_H, INV_PORT_C,
            INV_TO_H, INV_TO_C,
            RESTORE, RESTORE_H, RESTORE_C, RESTORE_BT, ALREADY_DEFAULT_H, ALREADY_DEFAULT_C,

    LANGUAGE, ENGLISH, FRENCH, ARABIC,

    DOWNLOAD_UPDATE, UPDATE, CHECK, DOWNLOAD, DOWNLOAD_NOW,
            DU_XO_H, DU_CHECKERS_H, DU_CHESS_H, DU_CONNECT4_H, DU_DOMINOS_H, DU_COINCHE_H,
            DU_XO_C, DU_CHECKERS_C, DU_CHESS_C, DU_CONNECT4_C, DU_DOMINOS_C, DU_COINCHE_C,

    ABOUT, ABOUT_ME, ABOUT_T, ABOUT_H, ABOUT_C, VISIT_GH, VISIT_LI,
            HELP, HELP_H, HELP_C, HELP_BT,
            FEEDBACK, FEEDBACK_H, FEEDBACK_C, FEEDBACK_BT,
            COPYRIGHT, COPYRIGHT_T, COPYRIGHT_H, COPYRIGHT_C, COPYRIGHT_BT,

    HOST, HOST_H, ALL_TAKEN,
            USERNAME, ENTER_UN, UN_H1, UN_C1, UN_H2, UN_C2,
            RID_H1, RID_C1, RID_H2, RID_C2,
            JOIN_PUB, JOIN_SPEC, ROOM_ID,

    PLAYERS, REFRESH, RET_HOME,

    ROOM_H, ROOM_C,
            CH_NAME, NAME_H, NAME_C,
            COPY,
            TAKE_PLACE, READY, START_GAME,
            HOST_PRIV,
            ROOM_PR, PRIVATE, PUBLIC,
            KICK_PLAYER, KICK_H1, KICK_C1, KICK_H2, KICK_C2,

    CHAT_T, CHAT_H1, CHAT_C1, CHAT_H2, CHAT_C2,

    NOTIFICATIONS, JOINED_NOTIF, LEFT_NOTIF;

    public static void init() {
        COINCHE = new SimpleStringProperty();
        DOMINOS = new SimpleStringProperty();
        CONNECT4 = new SimpleStringProperty();
        XO = new SimpleStringProperty();
        CHESS = new SimpleStringProperty();
        CHECKERS = new SimpleStringProperty();
        SKIP_H = new SimpleStringProperty();
        SKIP_C = new SimpleStringProperty();
        GO_BACK = new SimpleStringProperty();
        SKIP_BT = new SimpleStringProperty();
        LANG_H = new SimpleStringProperty();
        LANG_C = new SimpleStringProperty();
        THEME_H = new SimpleStringProperty();
        THEME_C = new SimpleStringProperty();
        LIGHT = new SimpleStringProperty();
        DARK = new SimpleStringProperty();
        WELCOME_H = new SimpleStringProperty();
        WELCOME_C = new SimpleStringProperty();
        WELCOME_BACK = new SimpleStringProperty();
        FINAL_PHASE_H = new SimpleStringProperty();
        FINAL_PHASE_C = new SimpleStringProperty();
        HOLD_H = new SimpleStringProperty();
        HOLD_C = new SimpleStringProperty();
        LOCAL_SETUP_H = new SimpleStringProperty();
        LOCAL_SETUP_C = new SimpleStringProperty();
        ONLINE_SETUP_H = new SimpleStringProperty();
        ONLINE_SETUP_C = new SimpleStringProperty();
        ONLINE_SETUP_SUCCESS = new SimpleStringProperty();
        ONLINE_SETUP_FAIL_H = new SimpleStringProperty();
        ONLINE_SETUP_FAIL_C = new SimpleStringProperty();
        LOCAL_SETUP_SUCCESS = new SimpleStringProperty();
        LOCAL_SETUP_FAIL_H = new SimpleStringProperty();
        LOCAL_SETUP_FAIL_C = new SimpleStringProperty();
        LOCAL = new SimpleStringProperty();
        ONLINE = new SimpleStringProperty();
        CANCEL = new SimpleStringProperty();
        OK = new SimpleStringProperty();
        ACCEPT = new SimpleStringProperty();
        GS_NA_C = new SimpleStringProperty();
        RETRY = new SimpleStringProperty();
        NETWORK = new SimpleStringProperty();
        CH_ON_IP = new SimpleStringProperty();
        CH_ON_PORT = new SimpleStringProperty();
        CH_ON_TO = new SimpleStringProperty();
        CH_MC_IP = new SimpleStringProperty();
        CH_JN_PORT = new SimpleStringProperty();
        CH_HS_PORT = new SimpleStringProperty();
        CH_LC_TO = new SimpleStringProperty();
        CH_ON_IP_H = new SimpleStringProperty();
        CH_MC_IP_H = new SimpleStringProperty();
        CH_PORT_H = new SimpleStringProperty();
        CH_TO_H = new SimpleStringProperty();
        SAME_JH_H = new SimpleStringProperty();
        SAME_JH_C1 = new SimpleStringProperty();
        SAME_JH_C2 = new SimpleStringProperty();
        INV_IP_H = new SimpleStringProperty();
        INV_IP_C = new SimpleStringProperty();
        INV_MC_IP_H = new SimpleStringProperty();
        INV_MC_IP_C = new SimpleStringProperty();
        INV_PORT_H = new SimpleStringProperty();
        INV_PORT_C = new SimpleStringProperty();
        INV_TO_H = new SimpleStringProperty();
        INV_TO_C = new SimpleStringProperty();
        RESTORE = new SimpleStringProperty();
        RESTORE_H = new SimpleStringProperty();
        RESTORE_C = new SimpleStringProperty();
        RESTORE_BT = new SimpleStringProperty();
        ALREADY_DEFAULT_H = new SimpleStringProperty();
        ALREADY_DEFAULT_C = new SimpleStringProperty();
        WINDOW = new SimpleStringProperty();
        SWITCH_FS = new SimpleStringProperty();
        LANGUAGE = new SimpleStringProperty();
        ENGLISH = new SimpleStringProperty();
        FRENCH = new SimpleStringProperty();
        ARABIC = new SimpleStringProperty();
        ARABIC.set("العربية");
        FRENCH.set("Francais");
        ENGLISH.set("English");
        SWITCH_THEME = new SimpleStringProperty();
        SHORTCUTS = new SimpleStringProperty();
        GENERAL = new SimpleStringProperty();
        MAIN = new SimpleStringProperty();
        JOIN = new SimpleStringProperty();
        ROOM = new SimpleStringProperty();
        GAME = new SimpleStringProperty();
        ABOUT = new SimpleStringProperty();
        HELP = new SimpleStringProperty();
        ABOUT_ME = new SimpleStringProperty();
        FEEDBACK = new SimpleStringProperty();
        COPYRIGHT = new SimpleStringProperty();
        GEN_SH_T = new SimpleStringProperty();
        GEN_SH_H = new SimpleStringProperty();
        GEN_SH_C = new SimpleStringProperty();
        M_SH_T = new SimpleStringProperty();
        M_SH_H = new SimpleStringProperty();
        M_SH_C = new SimpleStringProperty();
        HOST = new SimpleStringProperty();
        JOIN_PUB = new SimpleStringProperty();
        JOIN_SPEC = new SimpleStringProperty();
        ROOM_ID = new SimpleStringProperty();
        J_SH_T = new SimpleStringProperty();
        J_SH_H = new SimpleStringProperty();
        J_SH_C = new SimpleStringProperty();
        REFRESH = new SimpleStringProperty();
        RET_HOME = new SimpleStringProperty();
        R_SH_T = new SimpleStringProperty();
        R_SH_H1 = new SimpleStringProperty();
        R_SH_H2 = new SimpleStringProperty();
        R_SH_C1 = new SimpleStringProperty();
        R_SH_C2 = new SimpleStringProperty();
        OPEN_CH = new SimpleStringProperty();
        CH_NAME = new SimpleStringProperty();
        NAME_H = new SimpleStringProperty();
        NAME_C = new SimpleStringProperty();
        TAKE_PLACE = new SimpleStringProperty();
        NEXT = new SimpleStringProperty();
        PREVIOUS = new SimpleStringProperty();
        DONE = new SimpleStringProperty();
        KICK_PLAYER = new SimpleStringProperty();
        START_GAME = new SimpleStringProperty();
        G_SH_T = new SimpleStringProperty();
        G_SH_H = new SimpleStringProperty();
        G_SH_C = new SimpleStringProperty();
        VIEW_SC = new SimpleStringProperty();
        END_GAME = new SimpleStringProperty();
        DOWNLOAD_UPDATE = new SimpleStringProperty();
        UPDATE = new SimpleStringProperty();
        CHECK = new SimpleStringProperty();
        DOWNLOAD = new SimpleStringProperty();
        DU_XO_H = new SimpleStringProperty();
        DU_CHECKERS_H = new SimpleStringProperty();
        DU_CHESS_H = new SimpleStringProperty();
        DU_CONNECT4_H = new SimpleStringProperty();
        DU_DOMINOS_H = new SimpleStringProperty();
        DU_COINCHE_H = new SimpleStringProperty();
        DU_XO_C = new SimpleStringProperty();
        DU_CHECKERS_C = new SimpleStringProperty();
        DU_CHESS_C = new SimpleStringProperty();
        DU_CONNECT4_C = new SimpleStringProperty();
        DU_DOMINOS_C = new SimpleStringProperty();
        DU_COINCHE_C = new SimpleStringProperty();
        DOWNLOAD_NOW = new SimpleStringProperty();
        ABOUT_T = new SimpleStringProperty();
        ABOUT_H = new SimpleStringProperty();
        ABOUT_C = new SimpleStringProperty();
        VISIT_GH = new SimpleStringProperty();
        VISIT_LI = new SimpleStringProperty();
        HELP_H = new SimpleStringProperty();
        HELP_C = new SimpleStringProperty();
        HELP_BT = new SimpleStringProperty();
        FEEDBACK_H = new SimpleStringProperty();
        FEEDBACK_C = new SimpleStringProperty();
        FEEDBACK_BT = new SimpleStringProperty();
        COPYRIGHT_T = new SimpleStringProperty();
        COPYRIGHT_H = new SimpleStringProperty();
        COPYRIGHT_C = new SimpleStringProperty();
        COPYRIGHT_BT = new SimpleStringProperty();
        USERNAME = new SimpleStringProperty();
        UN_H1 = new SimpleStringProperty();
        UN_C1 = new SimpleStringProperty();
        UN_H2 = new SimpleStringProperty();
        UN_C2 = new SimpleStringProperty();
        RID_H1 = new SimpleStringProperty();
        RID_C1 = new SimpleStringProperty();
        RID_H2 = new SimpleStringProperty();
        RID_C2 = new SimpleStringProperty();
        HOST_H = new SimpleStringProperty();
        CHK_INTERNET = new SimpleStringProperty();
        CHK_LOCAL = new SimpleStringProperty();
        ALL_TAKEN = new SimpleStringProperty();
        ROOM_H = new SimpleStringProperty();
        ROOM_C = new SimpleStringProperty();
        COMM_ERROR_H = new SimpleStringProperty();
        COMM_ERROR_C = new SimpleStringProperty();
        CHAT_T = new SimpleStringProperty();
        CHAT_H1 = new SimpleStringProperty();
        CHAT_C1 = new SimpleStringProperty();
        CHAT_H2 = new SimpleStringProperty();
        CHAT_C2 = new SimpleStringProperty();
        GR_H = new SimpleStringProperty();
        DRAWS = new SimpleStringProperty();
        JOIN_ERROR = new SimpleStringProperty();
        GONE_PRIVATE = new SimpleStringProperty();
        NO_ROOMS = new SimpleStringProperty();
        PLAYERS = new SimpleStringProperty();
        ENTER_UN = new SimpleStringProperty();
        COPY = new SimpleStringProperty();
        NOTIFICATIONS = new SimpleStringProperty();
        JOINED_NOTIF = new SimpleStringProperty();
        LEFT_NOTIF = new SimpleStringProperty();
        MIGRATION_H1 = new SimpleStringProperty();
        MIGRATION_C1 = new SimpleStringProperty();
        MIGRATION_H2 = new SimpleStringProperty();
        MIGRATION_C2 = new SimpleStringProperty();
        HOST_PRIV = new SimpleStringProperty();
        ROOM_PR = new SimpleStringProperty();
        PRIVATE = new SimpleStringProperty();
        PUBLIC = new SimpleStringProperty();
        KICK_H1 = new SimpleStringProperty();
        KICK_C1 = new SimpleStringProperty();
        KICK_H2 = new SimpleStringProperty();
        KICK_C2 = new SimpleStringProperty();
        CNT_ACCESS_H = new SimpleStringProperty();
        CNT_ACCESS_C = new SimpleStringProperty();
        GE_H = new SimpleStringProperty();
        GE_C = new SimpleStringProperty();
        CNT_SG_H = new SimpleStringProperty();
        CNT_SG_C1 = new SimpleStringProperty();
        CNT_SG_C2 = new SimpleStringProperty();
        READY = new SimpleStringProperty();
    }

    public static void load_lang(LANGNAME lang) {
        LANG_NAME = lang;
        switch (lang) {
            case ENGLISH:
                load_english_strings();
                break;
            case ARABIC:
                load_arabic_strings();
                break;
            case FRENCH:
                load_french_strings();
                break;
        }
    }

    public static String unread_msg(int no) {
        switch (LANG_NAME) {
            case ENGLISH:
                return "You have " + no + " unread messages, open chat to read them !";
            case FRENCH:
                return "Vous avez " + no + " messages non lus, ouvrez le chat pour les lire !";
            case ARABIC:
                return "لديك " + no + " رسائل غير مقروءة ، افتح الدردشة لقراءتها !";
        }
        return null;
    }

    public static String kicked_notif(String name, boolean isYou) {
        if (isYou)
            switch (LANG_NAME) {
                case ENGLISH:
                    return "You were kicked of the room";
                case FRENCH:
                    return "Vous avez été expulsé de la salle";
                case ARABIC:
                    return "تم طردك من الغرفة";
            }
        else
            switch (LANG_NAME) {
                case ENGLISH:
                    return name + " was kicked of the room";
                case FRENCH:
                    return name + " a été expulsé de la salle";
                case ARABIC:
                    return "تم طرد " + name + " من الغرفة";
            }
        return null;
    }

    public static StringProperty unav() {
        StringProperty unav = new SimpleStringProperty();
        switch (LANG_NAME) {
            case ENGLISH:
                unav.set(MainApp.GAME_NAME + " server is not available at the moment");
            case FRENCH:
                unav.set("le serveur " + MainApp.GAME_NAME + " n'est pas disponible pour le moment");
            case ARABIC:
                unav.set("خادم " + MainApp.GAME_NAME + " غير متوفر في الوقت الحالي");
        }
        return unav;
    }

    private static void load_arabic_strings() {
        COINCHE.set("البلوت الفرنسية");
        DOMINOS.set("الدومينو");
        CONNECT4.set("4 على التوالي");
        XO.set("تيك تاك تو");
        CHESS.set("شطرنج");
        CHECKERS.set("لعبة الداما");
        SKIP_H.set("هل تريد حقًا تخطي البرنامج التعليمي؟");
        SKIP_C.set("هذه تجربة فريدة ، يرجى إكمالها من أجل مصلحتك ،"
                + "\n"
                + " لن يستغرق الأمر وقتًا طويلاً!");
        GO_BACK.set("تابع البرنامج التعليمي");
        SKIP_BT.set("تخطي البرنامج التعليمي");
        LANG_H.set("مرحبا! اي لغة تفضل؟");
        LANG_C.set("الرجاء اختيار لغتك المفضلة للمتابعة ، يمكن دائمًا تغيير هذا لاحقًا");
        THEME_H.set("ما هو الوضع الذي تفضله؟");
        THEME_C.set("اختر بين الوضع الفاتح والداكن لواجهة المستخدم، ويمكن تغيير ذلك لاحقًا");
        LIGHT.set("فاتح");
        DARK.set("داكن");
        WELCOME_H.set("نحن على وشك الانتهاء! اسمحوا لي أن أقدم لكم لعبتي");
        WELCOME_C.set("أهلا! أنا بن حاج عمار بلال ، مطور هذه اللعبة"
                + "\n"
                + "\n"
                + "لعبة 4 على التوالي الخاصة بي ليست لعبة عادية!"
                + "\n"
                + "يأتي في موضوعين وثلاث لغات"
                + "\n"
                + "إنه يوفر تعدد لاعبين حقيقي ، بدون ذكاء اصطناعي ، فقط أشخاص حقيقيون"
                + "\n"
                + "يمكن لعبها محليًا ، أو عبر الإنترنت"
                + "\n"
                + "يحتوي على تطبيق دردشة مدمج حتى تتمكن من تكوين صداقات جديدة ، والبقاء على اتصال أثناء اللعب"
                + "\n"
                + "\n"
                + "للحصول على أفضل تجربة ، لا تنس البحث عن التحديثات من وقت لآخر"
                + "\n");
        WELCOME_BACK.set("مرحبا بعودتك! :)");
        FINAL_PHASE_H.set("لتفعيل واجهة المستخدم ، حدد الوضع المحلي أو المتصل");
        FINAL_PHASE_C.set("شكرا لصبرك ، استمتع! ؛)");
        HOLD_H.set("الرجاء الانتظار...");
        HOLD_C.set("شكرا");
        ONLINE_SETUP_H.set("تهيئة الوضع المتصل");
        ONLINE_SETUP_C.set("جاري الاتصال بالخادم...");
        LOCAL_SETUP_H.set("تهيئة الوضع المحلي");
        LOCAL_SETUP_C.set("الرجاء الإنتظار...");
        ONLINE_SETUP_SUCCESS.set("تم تهيئة الوضع المتصل بنجاح");
        ONLINE_SETUP_FAIL_H.set("فشل تهيئة الوضع المتصل!");
        ONLINE_SETUP_FAIL_C.set("تأكد أنك متصل بالإنترنت ،"
                + "\n"
                + "ثم اختر واحدا مما يلي");
        LOCAL_SETUP_SUCCESS.set("تم تهيئة الوضع المحلي بنجاح");
        LOCAL_SETUP_FAIL_H.set("فشل تهيئة الوضع المحلي!");
        LOCAL_SETUP_FAIL_C.set("تأكد أنك متصل بشبكة محلية ،"
                + "\n"
                + "ثم اختر واحدا مما يلي");

        LOCAL.set("محلي");
        ONLINE.set("متصل");
        CANCEL.set("إلغاء");
        OK.set("حسنا");
        ACCEPT.set("قبول");
        GS_NA_C.set("يرجى العودة لاحقًا");
        RETRY.set("إعادة المحاولة");
        WINDOW.set("النافذة");
        SWITCH_FS.set("تبديل وضع ملء الشاشة");
        SWITCH_THEME.set("تبديل واجهة المستخدم");
        LANGUAGE.set("اللغة");
        SHORTCUTS.set("الاختصارات");
        GENERAL.set("الاختصارات العامة");
        MAIN.set("الرئيسية");
        JOIN.set("الانضمام");
        ROOM.set("الغرفة");
        GAME.set("اللعبة");
        ABOUT.set("عن البرنامج");
        HELP.set("مساعدة");
        ABOUT_ME.set("عن المطور");
        FEEDBACK.set("إبداء رأيك حول البرنامج");
        COPYRIGHT.set("حقوق التأليف");
        DOWNLOAD_UPDATE.set("تحميل/تحديث");
        VIEW_SC.set("عرض النتيجة");
        END_GAME.set("انهاء اللعبة");
        GEN_SH_T.set("الاختصارات العامة");
        GEN_SH_H.set("فيما يلي بعض الاختصارات العامة");
        GEN_SH_C.set("F1"
                + "\nكيف ألعب         "
                + "\n"
                + "\nF2"
                + "\nالتبديل بين المظهر الفاتح / الداكن         "
                + "\n"
                + "\nF11"
                + "\nتبديل وضع ملء الشاشة         ");
        M_SH_T.set("اختصارات الرئيسية");
        M_SH_H.set("فيما يلي بعض الاختصارات التي يمكنك استخدامها أثناء وجودك في القائمة الرئيسية");
        M_SH_C.set("Ctrl + H"
                + "\nاستضف غرفة         "
                + "\n"
                + "\nCtrl + J"
                + "\nالانضمام إلى الغرف العامة / المحلية         "
                + "\n"
                + "\nCtrl + O"
                + "\nالتبديل إلى الوضع المتصل         "
                + "\n"
                + "\nCtrl + L"
                + "\nالتبديل إلى الوضع المحلي         "
                + "\n"
                + "\nCtrl + R"
                + "\nالانضمام إلى غرفة محددة (الوضع المتصل فقط)         ");
        J_SH_T.set("اختصارات الانضمام");
        J_SH_H.set("فيما يلي بعض الاختصارات التي يمكنك استخدامها أثناء وجودك في قائمة الانضمام");
        J_SH_C.set("Ctrl + NUMPAD (1-5)"
                + "\nالانضمام إلى الغرفة رقم         "
                + "\n"
                + "\nCtrl + R"
                + "\nتحديث         "
                + "\n"
                + "\nCtrl + N"
                + "\nإظهار الغرف التالية (الوضع المحلي فقط)         "
                + "\n"
                + "\nCtrl + H"
                + "\nالرجوع إلى الرئيسية         ");
        R_SH_T.set("اختصارات الغرفة");
        R_SH_H1.set("إليك بعض الاختصارات التي يمكنك استخدامها ما دمت في غرفة"
                + "\n"
                + ", انقر التالي للمزيد");
        R_SH_H2.set("إليك بعض الاختصارات التي يمكنك استخدامها ما دمت في غرفة");
        R_SH_C1.set("Ctrl + C"
                + "\nفتح الدردشة (متاح أيضًا أثناء اللعب)         "
                + "\n"
                + "\nCtrl + N"
                + "\nتغيير الإسم         "
                + "\n"
                + "\nCtrl + Arrow keys (أسفل ، يمين ، أعلى ، يسار)"
                + "\nإحجز المكان الفارغ         "
                + "\n"
                + "\nCtrl + I"
                + "\nنسخ معرف الغرفة (الوضع المتصل فقط)         "
                + "\n"
                + "\nCtrl + R"
                + "\nتبديل حالة الاستعداد         ");
        R_SH_C2.set("Ctrl + T"
                + "\nإظهار / حجب الإشعارات         "
                + "\n"
                + "\nCtrl + K"
                + "\nطرد شخص ما (للمشرفين فقط) (متاح أيضًا أثناء اللعب)         "
                + "\n"
                + "\nCtrl + P"
                + "\nتغيير الخصوصية (للمسؤولين فقط)         "
                + "\n"
                + "\nCtrl + S"
                + "\nبدء اللعبة (للمشرفين فقط)         "
                + "\n"
                + "\nCtrl + H"
                + "\nالرجوع إلى الرئيسية         ");
        NEXT.set("التالي");
        PREVIOUS.set("السابق");
        DONE.set("إنتهاء");
        G_SH_T.set("اختصارات اللعبة");
        G_SH_H.set("فيما يلي بعض الاختصارات التي يمكن استخدامها أثناء اللعب");
        G_SH_C.set("Ctrl + V" +
                "\nعرض النتيجة         "
                + "\n"
                + "\nCtrl + E"
                + "\nانهاء اللعبة (العودة للغرفة)         ");

        NETWORK.set("الشبكة");
        CH_ON_IP.set("تغيير عنوان IP للخادم");
        CH_ON_PORT.set("تغيير منفذ الخادم");
        CH_ON_TO.set("تغيير مهلة الإتصال للوضع المتصل");
        CH_MC_IP.set("تغيير عنوان IP الإرسال المتعدد");
        CH_JN_PORT.set("تغيير منفذ المنخرطين");
        CH_HS_PORT.set("تغيير منفذ المضيفين");
        CH_LC_TO.set("تغيير مهلة الإتصال للوضع المحلي");
        CH_ON_IP_H.set("أدخل عنوان IP صالح"
                + "\n"
                + "\n"
                + "إذا كنت لا تعرف ما تفعله ، فاترك كل شيء في الإعدادات الافتراضية."
                + "\n"
                + "يمكنك دائمًا استعادة الإعدادات الافتراضية في قائمة الشبكة");
        CH_MC_IP_H.set("أدخل عنوان IP صالح للبث المتعدد"
                + "\n"
                + "تأكد من إدخال عنوان فريد لكل لعبة من ألعابي في الشبكة المحلية الخاصة بك"
                + "\n"
                + "إذا كنت لا تعرف ما تفعله ، فاترك كل شيء في الإعدادات الافتراضية."
                + "\n"
                + "يمكنك دائمًا استعادة الإعدادات الافتراضية في قائمة الشبكة");
        CH_PORT_H.set("أدخل رقم منفذ صالح"
                + "\n"
                + "\n"
                + "إذا كنت لا تعرف ما تفعله ، فاترك كل شيء في الإعدادات الافتراضية."
                + "\n"
                + "يمكنك دائمًا استعادة الإعدادات الافتراضية في قائمة الشبكة");
        CH_TO_H.set("أدخل مهلة صالحة (بالمللي ثانية)"
                + "\n"
                + "\n"
                + "إذا كنت لا تعرف ما تفعله ، فاترك كل شيء في الإعدادات الافتراضية."
                + "\n"
                + "يمكنك دائمًا استعادة الإعدادات الافتراضية في قائمة الشبكة");
        SAME_JH_H.set("لا يمكنك تعيين نفس المنفذ المنخرطين والمضيفين!");
        SAME_JH_C1.set("هذا المنفذ محجوز لمنفذ المنخرطين ، يرجى إدخال منفذ آخر");
        SAME_JH_C2.set("هذا المنفذ محجوز لمنفذ المضيفين ، يرجى إدخال منفذ آخر");
        INV_IP_H.set("عنوان IP غير صالح!");
        INV_IP_C.set("عناوين IP الصالحة تكون بالشكل: x.x.x.x"
                + "\n"
                + "حيث x بين (0-255)");
        INV_MC_IP_H.set("عنوان IP غير صالح للإرسال المتعدد!");
        INV_MC_IP_C.set("تبدأ عناوين IP الصالحة للإرسال المتعدد برقم من 224 إلى 239"
                + "\n"
                + "حيث x بين (0-255)");
        INV_PORT_H.set("رقم المنفذ غير صحيح!");
        INV_PORT_C.set("تتراوح أرقام المنافذ الصالحة من 1 إلى 65535");
        INV_TO_H.set("مهلة الاتصال غير صالحة!");
        INV_TO_C.set("تتراوح مهلات الاتصال الصالحة من 100 إلى 9999 مللي ثانية");

        RESTORE.set("استعادة الإعدادات الافتراضية");
        RESTORE_H.set("هل تريد استعادة الإعدادات الافتراضية؟");
        RESTORE_C.set("لا يمكن إسترجاع الإعدادات الحالية!");
        RESTORE_BT.set("استعادة");
        ALREADY_DEFAULT_H.set("أنت تستخدم بالفعل الإعدادات الافتراضية!");
        ALREADY_DEFAULT_C.set("لم يتم تغيير الإعدادات الافتراضية حتى الآن");
        UPDATE.set("تحديث");
        DOWNLOAD.set("تحميل");
        CHECK.set("التحقق");
        DOWNLOAD_NOW.set("حمل الان");
        DU_XO_H.set("تريد أن تلعب تيك تاك تو مع أصدقائك؟");
        DU_XO_C.set("قم بتنزيل لعبة تيك تاك تو الخاصة بي مجانًا");
        DU_CHECKERS_H.set("تريد أن تلعب الداما مع أصدقائك؟");
        DU_CHECKERS_C.set("قم بتنزيل لعبة الداما الخاصة بي مجانًا");
        DU_CHESS_H.set("تريد أن تلعب الشطرنج مع أصدقائك؟");
        DU_CHESS_C.set("قم بتنزيل لعبة الشطرنج الخاصة بي مجانًا");
        DU_CONNECT4_H.set("تأكد أن لديك أحدث إصدار من 4 على التوالي");
        DU_CONNECT4_C.set("نسختك الحالية هي: " + MainApp.CURRENT_VERSION + " ، تحقق من وجود إصدار جديد");
        DU_DOMINOS_H.set("تريد أن تلعب الدومينو مع أصدقائك؟");
        DU_DOMINOS_C.set("قم بتنزيل لعبة الدومينو الخاصة بي مجانًا");
        DU_COINCHE_H.set("تريد أن تلعب بلوت الفرنسية مع أصدقائك؟");
        DU_COINCHE_C.set("قم بتنزيل لعبة بلوت الفرنسية الخاصة بي مجانًا");
        ABOUT_T.set("بلال بن. يرسل تحياته");
        ABOUT_H.set("مرحبا! أنا بن حاج أعمر بلال");
        ABOUT_C.set("شكرا لاستخدام لعبتي :)\n" +
                "معرفة المزيد عني:");
        VISIT_GH.set("زيارة حسابي في GitHub");
        VISIT_LI.set("زيارة حسابي في LinkedIn");
        HELP_H.set("لا تعرف كيفية استخدام التطبيق؟");
        HELP_C.set("تعلم كيفية اللعب");
        HELP_BT.set("انا بحاجة الى مساعدة!");
        FEEDBACK_H.set("ما رأيك في هذا المشروع؟ اترك أفكارك / توصياتك!");
        FEEDBACK_C.set("يجب أن يكون لديك حساب على GitHub");
        FEEDBACK_BT.set("إيداع رأيك!");
        COPYRIGHT_T.set("حقوق التأليف");
        COPYRIGHT_H.set("تحتوي هذه اللعبة على شفرة مصدر غير مرخصة عن عمد (وليست مفتوحة المصدر) ،"
                + "\n"
                + "لا أعتبره إلا مشروعًا جانبيًا شخصيًا وطريقة لعرض مهاراتي."
                + "\n"
                + "يمكنك بالتأكيد لعب لعبتي بكل سرور ، أو مشاهدة كيف صنعتها على GitHub.");
        COPYRIGHT_C.set("لكني لا أقبل أي نوع من الاستخدام (تجاري ، براءة اختراع ، خاص) ،"
                + "\n"
                + "، توزيع أو تعديل الكود المصدري لهذه اللعبة."
                + "\n"
                + "\n"
                + "للحصول على اتفاقية ترخيص خاصة ، يرجى الاتصال بي على: bilel.bha.pro@gmail.com");
        COPYRIGHT_BT.set("شاهد المشروع على GitHub");

        USERNAME.set("اسم المستخدم");
        HOST.set("استضف غرفة");
        JOIN_PUB.set("انضم إلى الغرف العامة");
        ROOM_ID.set("معرف الغرفة");
        JOIN_SPEC.set("انضم الى الغرفة");
        UN_H1.set("لا يمكن لإسم المستخدم أن يكون فارغا!");
        UN_C1.set("أسماء المستخدمين الصالحة تتكون من 1 إلى 20 حرفًا");
        UN_H2.set("اسم المستخدم طويل جدا!");
        UN_C2.set("يجب ألا يتجاوز اسم المستخدم 20 حرفًا");
        RID_H1.set("معرف الغرفة فارغ!");
        RID_C1.set("أدخل معرف غرفة للانضمام إلى غرفة");
        RID_H2.set("معرف الغرفة غير صالح");
        RID_C2.set("هذه الغرفة غير موجودة!");
        HOST_H.set("لا يمكنك استضافة غرفة");
        CHK_INTERNET.set("تأكد أنك متصل بالإنترنت");
        CHK_LOCAL.set("تأكد أنك متصل بشبكة محلية");
        ALL_TAKEN.set("لسوء الحظ ، جميع الغرف مأخوذة."
                + "\n"
                + "يرجى المحاولة لاحقًا ، أو التبديل إلى الوضع المحلي");
        ROOM_H.set("لا يمكنك الانضمام إلى الغرفة");
        ROOM_C.set("إما أنها ممتلئة أو غير موجودة");
        COMM_ERROR_H.set("هناك خطأ ما!");
        COMM_ERROR_C.set("حدث خطأ أثناء الاتصال بالخادم");

        CHAT_T.set("الدردشة");
        CHAT_H1.set("رسالة فارغة!");
        CHAT_C1.set("أدخل نص لإرساله");
        CHAT_H2.set("الرسالة التي أدخلتها طويلة جدًا!");
        CHAT_C2.set("الرجاء إرسال ما لا يزيد عن 50 حرفًا في المرة الواحدة");

        GR_H.set("نتائج اللعبة");
        DRAWS.set("تعادلات : ");

        REFRESH.set("تحديث");
        RET_HOME.set("الرجوع إلى الرئيسية");
        JOIN_ERROR.set("حدث خطأ عند الانضمام إلى الغرفة");
        GONE_PRIVATE.set("أصبحت هذه الغرفة خاصة");
        NO_ROOMS.set("لا توجد غرف متاحة في الوقت الحالي ،\nيرجى العودة لاحقًا.");
        PLAYERS.set(" لاعبين");
        OPEN_CH.set("فتح الدردشة");
        CH_NAME.set("تغيير الإسم");
        NAME_H.set("لقد أدخلت نفس الاسم!");
        NAME_C.set("لديك هذا الاسم بالفعل");
        ENTER_UN.set("أدخل اسم مستخدم صالح");
        TAKE_PLACE.set("إحجز المكان");
        COPY.set("نسخ");
        NOTIFICATIONS.set("الإشعارات");

        HOST_PRIV.set("امتيازات المضيف");
        ROOM_PR.set("خصوصية الغرفة");
        PRIVATE.set("خاص");
        PUBLIC.set("عام");
        KICK_PLAYER.set("طرد لاعب");
        START_GAME.set("بدء اللعبة");

        KICK_H1.set("لا يمكنك طرد أي شخص");
        KICK_C1.set("أنت الوحيد في الغرفة!");
        KICK_H2.set("حدد اللاعب الذي تريد طرده من الغرفة");
        KICK_C2.set("لا يزال بإمكانه الانضمام إلى الغرفة");

        JOINED_NOTIF.set(" انضم الغرفة");
        LEFT_NOTIF.set(" غادر الغرفة");
        MIGRATION_H1.set("بدأ الترحيل");
        MIGRATION_C1.set("يرجى انتظار الترحيل إلى غرفة جديدة");
        MIGRATION_H2.set("انتهى الترحيل");
        MIGRATION_C2.set("أنت الآن متصل بغرفة المضيف الجديد");
        CNT_ACCESS_H.set("لا يمكنك الوصول إلى هذه الغرفة");
        CNT_ACCESS_C.set("الغرفة التي تحاول الوصول إليها مخصصة للعبة أخرى");
        GE_H.set("انتهت اللعبة!");
        GE_C.set("أدى حدث إلى إنهاء اللعبة");
        CNT_SG_H.set("لا يمكنك بدء اللعبة حاليا!");
        CNT_SG_C1.set("يجب أن يكون جميع اللاعبين جاهزين لبدء اللعبة");
        CNT_SG_C2.set("يرجى الانتظار حتى ينضم إليك خصمك");
        READY.set("حالة الاستعداد");
    }

    private static void load_french_strings() {
        COINCHE.set("Coinche");
        DOMINOS.set("Dominos");
        CONNECT4.set("Connecte 4");
        XO.set("xo");
        CHESS.set("Jeu d'échecs");
        CHECKERS.set("Dames");
        SKIP_H.set("Voulez-vous vraiment passer le tutoriel ?");
        SKIP_C.set("Il s'agit d'une expérience unique, veuillez la compléter pour votre intérêt,"
                + "\n"
                + " cela ne prendra pas longtemps!");
        GO_BACK.set("Continuer le tutoriel");
        SKIP_BT.set("Passer le tutoriel");
        LANG_H.set("Bienvenu ! quelle langue préférez-vous ?");
        LANG_C.set("Veuillez choisir votre langue préférée pour continuer,"
                + "\n"
                + " cela peut toujours être modifié plus tard");
        THEME_H.set("Quel thème préférez-vous");
        THEME_C.set("Choisissez entre le mode clair et sombre, cela peut toujours être modifié plus tard");
        LIGHT.set("Clair");
        DARK.set("Sombre");
        WELCOME_H.set("Nous sommes presque terminé ! laisse moi te présenter mon jeu");
        WELCOME_C.set("Salut! Je suis BENHADJ AMAR Bilel, le développeur de ce jeu"
                + "\n"
                + "\n"
                + "Mon jeu connecte 4 n'est pas un jeu ordinaire !"
                + "\n"
                + "Il se décline en deux thèmes et trois langues"
                + "\n"
                + "Il offre un vrai multijoueur, aucune IA incluse, seulement de vraies personnes"
                + "\n"
                + "Il peut être joué localement dans un réseau local ou en ligne via Internet"
                + "\n"
                + "Il contient une application de chat intégrée pour vous permettre d'avoir de nouveaux amis,"
                + "\n"
                + " et de rester connecté tout en jouant"
                + "\n"
                + "\n"
                + "Pour la meilleure expérience, n'oubliez pas de vérifier les mises à jour de temps en temps"
                + "\n");
        WELCOME_BACK.set("Content de te revoir! :)");
        FINAL_PHASE_H.set("Pour déverrouiller l'interface graphique,"
                + "\n"
                + " sélectionnez l'un des modes local ou en ligne");
        FINAL_PHASE_C.set("Merci pour votre patience, amusez-vous bien ! ;)");
        HOLD_H.set("Veuillez patienter un instant...");
        HOLD_C.set("Merci");
        ONLINE_SETUP_H.set("Configuration du mode en ligne");
        ONLINE_SETUP_C.set("Connexion au serveur...");
        LOCAL_SETUP_H.set("Configuration du mode local");
        LOCAL_SETUP_C.set("Veuillez patienter...");
        ONLINE_SETUP_SUCCESS.set("Le mode en ligne a été configuré avec succès");
        ONLINE_SETUP_FAIL_H.set("Échec de la configuration du mode en ligne !");
        ONLINE_SETUP_FAIL_C.set("Assurez-vous que vous êtes connecté à Internet,"
                + "\n"
                + "puis choisissez l'une des options suivantes");
        LOCAL_SETUP_SUCCESS.set("Le mode local a été configuré avec succès");
        LOCAL_SETUP_FAIL_H.set("Échec de la configuration du mode local !");
        LOCAL_SETUP_FAIL_C.set("Assurez-vous que vous êtes connecté à un réseau local,"
                + "\n"
                + "puis choisissez l'une des options suivantes");
        LOCAL.set("Local");
        ONLINE.set("En ligne");
        CANCEL.set("Annuler");
        OK.set("OK");
        ACCEPT.set("J'accepte");
        GS_NA_C.set("Merci de revenir plus tard");
        RETRY.set("Réessayez");
        WINDOW.set("Fenêtre");
        SWITCH_FS.set("Alterner le mode plein écran");
        SWITCH_THEME.set("Changer de thème");
        LANGUAGE.set("Langue");
        SHORTCUTS.set("Raccourcis");
        GENERAL.set("Général");
        MAIN.set("Menu principale");
        JOIN.set("Rejoindre");
        ROOM.set("Salle");
        GAME.set("Jeu");
        ABOUT.set("À propos");
        HELP.set("Aide");
        ABOUT_ME.set("À propos de moi");
        FEEDBACK.set("Donnez votre avis");
        COPYRIGHT.set("Notice de droits d'auteur");
        DOWNLOAD_UPDATE.set("Téléchargements/mise à jour");
        VIEW_SC.set("Voir le score");
        END_GAME.set("Terminer le jeu");
        GEN_SH_T.set("Raccourcis généraux");
        GEN_SH_H.set("Voici quelques raccourcis généraux");
        GEN_SH_C.set("F1"
                + "\n         Comment jouer"
                + "\n"
                + "\nF2"
                + "\n         Basculer entre le thème clair/sombre"
                + "\n"
                + "\nF11"
                + "\n         Alterner le mode plein écran");
        M_SH_T.set("Raccourcis de menu principale");
        M_SH_H.set("Voici quelques raccourcis à utiliser lorsque vous êtes dans le menu principal");
        M_SH_C.set("Ctrl + H"
                + "\n         Héberger une salle"
                + "\n"
                + "\nCtrl + J"
                + "\n         Rejoindre les salles publiques/locales"
                + "\n"
                + "\nCtrl + O"
                + "\n         Passer en mode en ligne"
                + "\n"
                + "\nCtrl + L"
                + "\n         Passer en mode local"
                + "\n"
                + "\nCtrl + R"
                + "\n         Rejoindre une salle spécifique (mode en ligne uniquement)");
        J_SH_T.set("Raccourcis de menu rejoindre");
        J_SH_H.set("Voici quelques raccourcis à utiliser lorsque vous êtes dans le menu rejoindre");
        J_SH_C.set("Ctrl + PAVÉ NUMÉRIQUE (1-5)"
                + "\n         Rejoignez la salle no."
                + "\n"
                + "\nCtrl + R"
                + "\n         Rafraîchir"
                + "\n"
                + "\nCtrl + N"
                + "\n         Afficher les salles suivantes (mode local uniquement)"
                + "\n"
                + "\nCtrl + H"
                + "\n         Retourner au menu principal");
        R_SH_T.set("Raccourcis des salles");
        R_SH_H1.set("Voici quelques raccourcis à utiliser tant que vous êtes dans une salle"
                + "\n"
                + ", cliquez sur suivant pour plus de raccourcis");
        R_SH_H2.set("Voici quelques raccourcis à utiliser tant que vous êtes dans une salle");
        R_SH_C1.set("Ctrl + C"
                + "\n         Ouvrir le chat (dispo. pendant le jeu)"
                + "\n"
                + "\nCtrl + N"
                + "\n         Changer votre nom"
                + "\n"
                + "\nCtrl + Flèches (Bas, Droite, Haut, Gauche)"
                + "\n         Prendre la place vide"
                + "\n"
                + "\nCtrl + I"
                + "\n         Copier l'id de la salle (mode en ligne uniquement)"
                + "\n"
                + "\nCtrl + R"
                + "\n         Basculer l'état prêt");
        R_SH_C2.set("Ctrl + T"
                + "\n         Afficher/Masquer les notifications"
                + "\n"
                + "\nCtrl + K"
                + "\n         Expulser quelqu'un (administrateurs uniquement) (dispo. pendant le jeu)"
                + "\n"
                + "\nCtrl + P"
                + "\n         Modifier la confidentialité (administrateurs uniquement)"
                + "\n"
                + "\nCtrl + S"
                + "\n         Commencer le jeu (administrateurs uniquement)"
                + "\n"
                + "\nCtrl + H"
                + "\n         Retourner au menu principal");
        NEXT.set("Suivant");
        PREVIOUS.set("Précédent");
        DONE.set("Fait");
        G_SH_T.set("Raccourcis de jeu");
        G_SH_H.set("Voici quelques raccourcis à utiliser pendant le jeu");
        G_SH_C.set("Ctrl + V" +
                "\n         Voir le score"
                + "\n"
                + "\nCtrl + E"
                + "\n         Terminer le jeu (retourner à la salle)");

        NETWORK.set("Réseau");
        CH_ON_IP.set("Changer l'ip du serveur");
        CH_ON_PORT.set("Changer le port du serveur");
        CH_ON_TO.set("Modifier le délai d'attente en ligne");
        CH_MC_IP.set("Changer l'adresse IP de multidiffusion");
        CH_JN_PORT.set("Changer le port des rejoindres");
        CH_HS_PORT.set("Changer le port d'hébergeurs");
        CH_LC_TO.set("Modifier le délai d'attente local");
        CH_ON_IP_H.set("Entrez une adresse IP valide"
                + "\n"
                + "\n"
                + "Si vous ne savez pas ce que vous faites, laissez tout dans les paramètres par défaut."
                + "\n"
                + "Vous pouvez toujours restaurer les paramètres par défaut dans le menu réseau");
        CH_MC_IP_H.set("Saisissez une adresse IP de multidiffusion valide"
                + "\n"
                + "Assurez-vous d'entrer une adresse unique pour chacun de mes jeux dans votre réseau local"
                + "\n"
                + "Si vous ne savez pas ce que vous faites, laissez tout dans les paramètres par défaut."
                + "\n"
                + "Vous pouvez toujours restaurer les paramètres par défaut dans le menu réseau");
        CH_PORT_H.set("Entrez un numéro de port valide"
                + "\n"
                + "\n"
                + "Si vous ne savez pas ce que vous faites, laissez tout dans les paramètres par défaut."
                + "\n"
                + "Vous pouvez toujours restaurer les paramètres par défaut dans le menu réseau");
        CH_TO_H.set("Saisissez un délai d'attente valide (en millisecondes)"
                + "\n"
                + "\n"
                + "Si vous ne savez pas ce que vous faites, laissez tout dans les paramètres par défaut."
                + "\n"
                + "Vous pouvez toujours restaurer les paramètres par défaut dans le menu réseau");
        SAME_JH_H.set("Vous ne pouvez pas définir le même port pour les rejoindres et les hébergeurs !");
        SAME_JH_C1.set("Ce port est réservé aux rejoindres, veuillez en saisir un autre");
        SAME_JH_C2.set("Ce port est réservé aux hébergeurs, veuillez en saisir un autre");
        INV_IP_H.set("Adresse IP invalide !");
        INV_IP_C.set("Les adresses IP valides sont au format: x.x.x.x"
                + "\n"
                + "où x est compris entre (0-255)");
        INV_MC_IP_H.set("Adresse IP de multidiffusion invalide !");
        INV_MC_IP_C.set("Les adresses IP de multidiffusion valides vont de 224.x.x.x à 239.x.x.x"
                + "\n"
                + "où x est compris entre (0-255)");
        INV_PORT_H.set("Numéro de port invalide !");
        INV_PORT_C.set("Les numéros de port valides sont compris entre 1 et 65535");
        INV_TO_H.set("Délai de connexion invalide !");
        INV_TO_C.set("Les délais de connexion valides sont de 100 ms à 9999 ms");
        RESTORE.set("Restaurer les paramètres par défaut");
        RESTORE_H.set("Voulez-vous restaurer les paramètres par défaut ?");
        RESTORE_C.set("Les paramètres actuels ne peuvent pas être restaurés");
        RESTORE_BT.set("Restaurer");
        ALREADY_DEFAULT_H.set("Vous utilisez déjà les paramètres par défaut !");
        ALREADY_DEFAULT_C.set("Les paramètres par défaut n'ont pas encore été modifiés");
        UPDATE.set("Mise à jour");
        DOWNLOAD.set("Télécharger");
        CHECK.set("Vérifier");
        DOWNLOAD_NOW.set("Télécharger maintenant");
        DU_XO_H.set("Voulez-vous jouer xo avec vos amis ?");
        DU_XO_C.set("Téléchargez mon jeu xo gratuitement");
        DU_CHECKERS_H.set("Voulez-vous jouer aux dames avec vos amis ?");
        DU_CHECKERS_C.set("Téléchargez mon jeu de dames gratuitement");
        DU_CHESS_H.set("Voulez-vous jouer aux jeu d'échecs avec vos amis ?");
        DU_CHESS_C.set("Téléchargez mon jeu d'échecs gratuitement");
        DU_CONNECT4_H.set("Assurez-vous d'avoir la dernière version de Connecte 4");
        DU_CONNECT4_C.set("Votre version actuelle est : "
                + MainApp.CURRENT_VERSION
                + " vérifier s'il y a une nouvelle version");
        DU_DOMINOS_H.set("Voulez-vous jouer aux dominos avec vos amis ?");
        DU_DOMINOS_C.set("Téléchargez mon jeu de dominos gratuitement");
        DU_COINCHE_H.set("Voulez-vous jouer aux coinche avec vos amis ?");
        DU_COINCHE_C.set("Téléchargez mon jeu de coinche gratuitement");
        ABOUT_T.set("BHA-Bilel envoie ses salutations");
        ABOUT_H.set("Bonjour! je suis BENHADJ AMAR Bilel");
        ABOUT_C.set("Merci d'avoir utiliser mon jeu :)\n" +
                "Trouves-en plus à propos de moi:");
        VISIT_GH.set("Visitez mon profil GitHub");
        VISIT_LI.set("Visitez mon profil LinkedIn");
        HELP_H.set("Vous ne savez pas comment utiliser mon jeu ?");
        HELP_C.set("Découvrez comment jouer");
        HELP_BT.set("J'ai besoin d'aide!");
        FEEDBACK_H.set("Que pensez-vous de ce projet ? laissez vos avis/recommandations !");
        FEEDBACK_C.set("Vous devez avoir un profil GitHub");
        FEEDBACK_BT.set("Donnez votre avis !");
        COPYRIGHT_T.set("Notice de droits d'auteur");
        COPYRIGHT_H.set("Ce jeu contient volontairement du code source SANS LICENCE (PAS open-source),"
                + "\n"
                + "que je considère seulement comme un projet secondaire personnel "
                + "\n"
                + "et un moyen de mettre en valeur mes compétences."
                + "\n"
                + "Vous pouvez sûrement et volontiers jouer à mon jeu, ou voir le code source sur GitHub.");
        COPYRIGHT_C.set("Cependant, JE N'ACCEPTE AUCUN type d'utilisation (commercial, brevet, privé),"
                + "\n"
                + "distribution ou modification du code source de ce jeu."
                + "\n"
                + "\n"
                + "Pour un accord de licence privé, veuillez me contacter à: bilel.bha.pro@gmail.com");
        COPYRIGHT_BT.set("Voir le projet sur GitHub");

        USERNAME.set("Nom d'utilisateur");
        HOST.set("Héberger une salle");
        JOIN_PUB.set("Rejoindre les salles publiques");
        ROOM_ID.set("identifiant de salle");
        JOIN_SPEC.set("Rejoindre la salle");
        UN_H1.set("Le nom d'utilisateur ne peut pas être vide !");
        UN_C1.set("Les noms d'utilisateur valides sont de 1 à 20 caractères");
        UN_H2.set("Nom d'utilisateur trop long !");
        UN_C2.set("Le nom d'utilisateur ne doit pas dépasser 20 caractères");
        RID_H1.set("Identifiant de salle vide !");
        RID_C1.set("Entrez un identifiant de salle pour rejoindre une salle");
        RID_H2.set("Identifiant de salle invalide");
        RID_C2.set("Cette salle n'existe pas !");
        HOST_H.set("Impossible d'héberger une salle");
        CHK_INTERNET.set("Assurez-vous que vous êtes connecté à Internet");
        CHK_LOCAL.set("Assurez-vous que vous êtes connecté à un réseau local");
        ALL_TAKEN.set("Malheureusement, toutes les salle sont prises."
                + "\n"
                + "Veuillez réessayer plus tard ou utiliser le mode local");
        ROOM_H.set("Impossible de rejoindre la salle");
        ROOM_C.set("Elle est plein ou n'existe pas");
        COMM_ERROR_H.set("Quelque chose s'est mal passé !");
        COMM_ERROR_C.set("Une erreur s'est produite lors de la communication avec le serveur");

        CHAT_T.set("Discuter");
        CHAT_H1.set("Message vide !");
        CHAT_C1.set("Entrez un message à envoyer");
        CHAT_H2.set("Le message que vous avez entré est trop long !");
        CHAT_C2.set("Veuillez ne pas envoyer plus de 50 caractères à la fois");

        GR_H.set("Résultats de partie");
        DRAWS.set("Égalisations : ");

        REFRESH.set("Rafraîchir");
        RET_HOME.set("Retourner au menu principal");
        JOIN_ERROR.set("Une erreur s'est produite lors de la connexion à la salle");
        GONE_PRIVATE.set("La salle est devenue privée");
        NO_ROOMS.set("Aucune chambre n'est disponible pour le moment,\nVeuillez réessayer plus tard.");
        PLAYERS.set(" joueurs");
        OPEN_CH.set("Ouvrir le chat");
        CH_NAME.set("Changer votre nom");
        NAME_H.set("Vous avez entré le même nom !");
        NAME_C.set("Vous avez déjà ce nom");
        ENTER_UN.set("Entrez un nom d'utilisateur valide");
        TAKE_PLACE.set("Prendre la place");
        COPY.set("Copier");
        NOTIFICATIONS.set("Notifications");

        HOST_PRIV.set("Privilèges d'hôte");
        ROOM_PR.set("Confidentialité de salle");
        PRIVATE.set("Privée");
        PUBLIC.set("Publique");
        KICK_PLAYER.set("Expulser un membre");
        START_GAME.set("Commencer le jeu");

        KICK_H1.set("Tu ne peux expulser personne");
        KICK_C1.set("Vous êtes la seul personne dans la salle !");
        KICK_H2.set("Sélectionnez le joueur que vous voulez expulser de la salle");
        KICK_C2.set("Il pourrait toujours rejoindre la salle");

        JOINED_NOTIF.set(" a rejoint la salle");
        LEFT_NOTIF.set(" a quitté la salle");
        MIGRATION_H1.set("Migration a commencé");
        MIGRATION_C1.set("Veuillez attendre la migration vers une nouvelle salle");
        MIGRATION_H2.set("Migration terminée");
        MIGRATION_C2.set("Vous êtes maintenant connecté à la  salle de nouveau hôte");
        CNT_ACCESS_H.set("Vous ne pouvez pas accéder à cette salle");
        CNT_ACCESS_C.set("La pièce à laquelle vous essayez d'accéder est destinée à un autre jeu");
        GE_H.set("Le jeu est terminé !");
        GE_C.set("Un événement a conduit le jeu à se terminer");
        CNT_SG_H.set("Vous ne pouvez pas encore commencer le jeu !");
        CNT_SG_C1.set("Tous les joueurs doivent être prêts à commencer le jeu");
        CNT_SG_C2.set("Veuillez attendre que votre adversaire vous rejoigne");
        READY.set("Prêt");
    }

    private static void load_english_strings() {
        COINCHE.set("French coinche");
        DOMINOS.set("Dominoes");
        CONNECT4.set("Connect 4");
        XO.set("xo");
        CHESS.set("Chess");
        CHECKERS.set("Checkers");
        SKIP_H.set("Do you really want to skip the tutorial?");
        SKIP_C.set("This is a one time experience, please complete it for your interest,"
                + "\n"
                + " it won't take long!");
        GO_BACK.set("Continue tutorial");
        SKIP_BT.set("Skip tutorial");
        LANG_H.set("Welcome! what language do you prefer?");
        LANG_C.set("Please choose your preferred language to proceed, this can always be changed later");
        THEME_H.set("What theme do you prefer");
        THEME_C.set("Choose between light and dark theme, this can always be changed later");
        LIGHT.set("Light");
        DARK.set("Dark");
        WELCOME_H.set("We're almost done! let me introduce you to my game");
        WELCOME_C.set("Hi! I am BENHADJ AMAR Bilel, the developer of this game"
                + "\n"
                + "\n"
                + "My connect 4 game is no ordinary game!"
                + "\n"
                + "It comes in two themes, and three languages"
                + "\n"
                + "It offers real multiplayer, no AI included, only real people"
                + "\n"
                + "It can be played locally in a local area network, or online through Internet"
                + "\n"
                + "It contains a built-in chat app so you can make new friends, and stay connected while playing"
                + "\n"
                + "\n"
                + "For the best experience, don't forget to check for updates from time to time"
                + "\n");
        WELCOME_BACK.set("Welcome back! :)");
        FINAL_PHASE_H.set("To unlock the gui, select either local or online mode");
        FINAL_PHASE_C.set("Thanks for your patience, have fun! ;)");
        HOLD_H.set("Please hold for a moment...");
        HOLD_C.set("Thank you");
        ONLINE_SETUP_H.set("Setting up online mode");
        ONLINE_SETUP_C.set("Connecting to server...");
        LOCAL_SETUP_H.set("Setting up local mode");
        LOCAL_SETUP_C.set("Please wait...");
        ONLINE_SETUP_SUCCESS.set("Online mode was setup successfully");
        ONLINE_SETUP_FAIL_H.set("Online mode setup failed!");
        ONLINE_SETUP_FAIL_C.set("Make sure you're connected to Internet,"
                + "\n"
                + "then choose one of the following");
        LOCAL_SETUP_SUCCESS.set("Local mode was setup successfully");
        LOCAL_SETUP_FAIL_H.set("Local mode setup failed!");
        LOCAL_SETUP_FAIL_C.set("Make sure you're connected to a local area network,"
                + "\n"
                + "then choose one of the following");
        LOCAL.set("Local");
        ONLINE.set("Online");
        CANCEL.set("Cancel");
        OK.set("OK");
        ACCEPT.set("I accept");
        GS_NA_C.set("Please come back later");
        RETRY.set("Retry");
        WINDOW.set("Window");
        SWITCH_FS.set("Toggle fullscreen mode");
        SWITCH_THEME.set("Switch theme");
        LANGUAGE.set("Language");
        SHORTCUTS.set("Shortcuts");
        GENERAL.set("General");
        MAIN.set("Main");
        JOIN.set("Join");
        ROOM.set("Room");
        GAME.set("Game");
        ABOUT.set("About");
        HELP.set("Help");
        ABOUT_ME.set("About me");
        FEEDBACK.set("Give feedback");
        COPYRIGHT.set("Copyright notice");
        DOWNLOAD_UPDATE.set("Download/Update");
        VIEW_SC.set("View score");
        END_GAME.set("End game");
        GEN_SH_T.set("General Shortcuts");
        GEN_SH_H.set("Here are some general shortcuts");
        GEN_SH_C.set("F1"
                + "\n         How to play"
                + "\n"
                + "\nF2"
                + "\n         Switch between light/dark theme"
                + "\n"
                + "\nF11"
                + "\n         Toggle fullscreen mode");
        M_SH_T.set("Main Shortcuts");
        M_SH_H.set("Here are some shortcuts to use while you're in main menu");
        M_SH_C.set("Ctrl + H"
                + "\n         Host a room"
                + "\n"
                + "\nCtrl + J"
                + "\n         Join public/local rooms"
                + "\n"
                + "\nCtrl + O"
                + "\n         Switch to online mode"
                + "\n"
                + "\nCtrl + L"
                + "\n         Switch to local mode"
                + "\n"
                + "\nCtrl + R"
                + "\n         Join specific room (online mode only)");
        J_SH_T.set("Join Shortcuts");
        J_SH_H.set("Here are some shortcuts to use while you're in join menu");
        J_SH_C.set("Ctrl + NUMPAD (1-5)"
                + "\n         Join room no."
                + "\n"
                + "\nCtrl + R"
                + "\n         Refresh"
                + "\n"
                + "\nCtrl + N"
                + "\n         Show next rooms (local mode only)"
                + "\n"
                + "\nCtrl + H"
                + "\n         Return home");
        R_SH_T.set("Room Shortcuts");
        R_SH_H1.set("Here are some shortcuts to use while you're in a room"
                + "\n"
                + ", click next for more");
        R_SH_H2.set("Here are some shortcuts to use while you're in a room");
        R_SH_C1.set("Ctrl + C"
                + "\n         Open chat (also available in-game)"
                + "\n"
                + "\nCtrl + N"
                + "\n         Change name"
                + "\n"
                + "\nCtrl + Arrow keys (Bottom, Right, Up, Left)"
                + "\n         Take empty place"
                + "\n"
                + "\nCtrl + I"
                + "\n         Copy room id (online mode only)"
                + "\n"
                + "\nCtrl + R"
                + "\n         Toggle ready status");
        R_SH_C2.set("Ctrl + T"
                + "\n         Show/Suppress notifications"
                + "\n"
                + "\nCtrl + K"
                + "\n         Kick someone (for admins only) (also available in-game)"
                + "\n"
                + "\nCtrl + P"
                + "\n         Change privacy (for admins only)"
                + "\n"
                + "\nCtrl + S"
                + "\n         Start game (for admins only)"
                + "\n"
                + "\nCtrl + H"
                + "\n         Return home");
        NEXT.set("Next");
        PREVIOUS.set("Previous");
        DONE.set("Done");
        G_SH_T.set("Game Shortcuts");
        G_SH_H.set("Here are some shortcuts to use while playing the game");
        G_SH_C.set("Ctrl + V" +
                "\n         View score"
                + "\n"
                + "\nCtrl + E"
                + "\n         End game (return to room)");

        NETWORK.set("Network");
        CH_ON_IP.set("Change server ip");
        CH_ON_PORT.set("Change server port");
        CH_ON_TO.set("Change online timeout");
        CH_MC_IP.set("Change multicast ip");
        CH_JN_PORT.set("Change joiners port");
        CH_HS_PORT.set("Change hosters port");
        CH_LC_TO.set("Change local timeout");
        CH_ON_IP_H.set("Enter a valid IP address"
                + "\n"
                + "\n"
                + "If you don't know what you're doing, leave everything in default settings."
                + "\n"
                + "You can always restore default settings in the network menu");
        CH_MC_IP_H.set("Enter a valid multicast IP address"
                + "\n"
                + "Make sure you enter a unique address for each of my games in your local area network"
                + "\n"
                + "If you don't know what you're doing, leave everything in default settings."
                + "\n"
                + "You can always restore default settings in the network menu");
        CH_PORT_H.set("Enter a valid port number"
                + "\n"
                + "\n"
                + "If you don't know what you're doing, leave everything in default settings."
                + "\n"
                + "You can always restore default settings in the network menu");
        CH_TO_H.set("Enter a valid timeout (in milliseconds)"
                + "\n"
                + "\n"
                + "If you don't know what you're doing, leave everything in default settings."
                + "\n"
                + "You can always restore default settings in the network menu");
        SAME_JH_H.set("You can't set the same port for joiners and hosters!");
        SAME_JH_C1.set("This port is reserved to joiners port, please enter a different one");
        SAME_JH_C2.set("This port is reserved to hosters port, please enter a different one");
        INV_IP_H.set("Invalid IP address!");
        INV_IP_C.set("Valid IP addresses are in form: x.x.x.x"
                + "\n"
                + "where x is between (0-255)");
        INV_MC_IP_H.set("Invalid multicast IP address!");
        INV_MC_IP_C.set("Valid multicast IP addresses are from 224.x.x.x to 239.x.x.x"
                + "\n"
                + "where x is between (0-255)");
        INV_PORT_H.set("Invalid port number!");
        INV_PORT_C.set("Valid port numbers are from 1 to 65535");
        INV_TO_H.set("Invalid connection timeout!");
        INV_TO_C.set("Valid connection timeouts are from 100 ms to 9999 ms");
        RESTORE.set("Restore default settings");
        RESTORE_H.set("Do you want to restore default settings?");
        RESTORE_C.set("The current settings cannot be restored");
        RESTORE_BT.set("Restore");
        ALREADY_DEFAULT_H.set("you're already using the default settings!");
        ALREADY_DEFAULT_C.set("Default settings have not been changed yet");
        UPDATE.set("Update");
        DOWNLOAD.set("Download");
        CHECK.set("Check");
        DOWNLOAD_NOW.set("Download now");
        DU_XO_H.set("Want to play xo with your friends?");
        DU_XO_C.set("Download my xo app for free");
        DU_CHECKERS_H.set("Want to play Checkers with your friends?");
        DU_CHECKERS_C.set("Download my Checkers app for free");
        DU_CHESS_H.set("Want to play Chess with your friends?");
        DU_CHESS_C.set("Download my Chess app for free");
        DU_CONNECT4_H.set("Make sure you have the latest version of connect 4");
        DU_CONNECT4_C.set("Your current version is : " + MainApp.CURRENT_VERSION + ", Check for new version");
        DU_DOMINOS_H.set("Want to play Dominoes with your friends?");
        DU_DOMINOS_C.set("Download my Dominoes app for free");
        DU_COINCHE_H.set("Want to play Coinche with your friends?");
        DU_COINCHE_C.set("Download my Coinche app for free");
        ABOUT_T.set("BHA-Bilel sends his regards");
        ABOUT_H.set("Hello! I am BENHADJ AMAR Bilel");
        ABOUT_C.set("Thanks for playing my game :)\n" +
                "Find out more about me:");
        VISIT_GH.set("Visit GitHub profile");
        VISIT_LI.set("Visit LinkedIn profile");
        HELP_H.set("You don't know how to use my app?");
        HELP_C.set("Check out how to play");
        HELP_BT.set("I need help!");
        FEEDBACK_H.set("What do you think of this project? leave your thoughts/recommendations!");
        FEEDBACK_C.set("You need to have a GitHub profile");
        FEEDBACK_BT.set("Give feedback!");
        COPYRIGHT_T.set("Copyright notice");
        COPYRIGHT_H.set("This game contain purposely UNLICENSED source code (NOT open-source),"
                + "\n"
                + "that I only consider as a personal side project and a way to showcase my skills."
                + "\n"
                + "You can surely and gladly play my game, or view how it's made on GitHub.");
        COPYRIGHT_C.set("However, I DO NOT grant any kind of usage (commercial, patent, private),"
                + "\n"
                + "distribution or modification of the source code of this game."
                + "\n"
                + "\n"
                + "For a private license agreement please contact me at: bilel.bha.pro@gmail.com");
        COPYRIGHT_BT.set("See project on GitHub");

        USERNAME.set("Username");
        HOST.set("Host a room");
        JOIN_PUB.set("Join public rooms");
        ROOM_ID.set("Room ID");
        JOIN_SPEC.set("Join room");
        UN_H1.set("Username can't be empty!");
        UN_C1.set("Valid usernames are from 1 to 20 characters");
        UN_H2.set("Username too long!");
        UN_C2.set("Username must not exceed 20 characters");
        RID_H1.set("Empty room ID!");
        RID_C1.set("Enter a room ID to join a room");
        RID_H2.set("Invalid room ID");
        RID_C2.set("This room doesn't exist!");
        HOST_H.set("Couldn't host a room");
        CHK_INTERNET.set("Make sure you're connected to Internet ");
        CHK_LOCAL.set("Make sure you're connected to a local area network");
        ALL_TAKEN.set("Unfortunately, all rooms are taken."
                + "\n"
                + "Please try again later, or switch to local mode");
        ROOM_H.set("Couldn't join the room");
        ROOM_C.set("It's either full or doesn't exist");
        COMM_ERROR_H.set("Something went wrong!");
        COMM_ERROR_C.set("An error has occurred while communicating with the server");

        CHAT_T.set("Chat");
        CHAT_H1.set("Empty message!");
        CHAT_C1.set("Enter a message to send");
        CHAT_H2.set("The message you entered is too long!");
        CHAT_C2.set("Please send no more than 50 characters at a time");

        GR_H.set("Game results");
        DRAWS.set("Draws : ");

        REFRESH.set("Refresh");
        RET_HOME.set("Return Home");
        JOIN_ERROR.set("An error has occurred when joining the room");
        GONE_PRIVATE.set("This room has gone private");
        NO_ROOMS.set("No rooms are available at the moment,\nPlease come back later.");
        PLAYERS.set(" players");
        OPEN_CH.set("Open chat");
        CH_NAME.set("Change name");
        NAME_H.set("You entered the same name!");
        NAME_C.set("You already have that name");
        ENTER_UN.set("Enter a valid username");
        TAKE_PLACE.set("Take place");
        COPY.set("Copy");
        NOTIFICATIONS.set("Notifications");

        HOST_PRIV.set("Host Privileges");
        ROOM_PR.set("Room Privacy");
        PRIVATE.set("Private");
        PUBLIC.set("Public");
        KICK_PLAYER.set("Kick a player");
        START_GAME.set("Start game");

        KICK_H1.set("You can't kick anyone");
        KICK_C1.set("You're the only one in the room!");
        KICK_H2.set("Select the player you want to kick from the room");
        KICK_C2.set("He could still rejoin the room");

        JOINED_NOTIF.set(" joined the room");
        LEFT_NOTIF.set(" left the room");
        MIGRATION_H1.set("Migration started");
        MIGRATION_C1.set("Please wait for migration to new room");
        MIGRATION_H2.set("Migration finished");
        MIGRATION_C2.set("you're now connected to new host's room");
        CNT_ACCESS_H.set("You can't access this room");
        CNT_ACCESS_C.set("The room you're trying to access is meant for another game");
        GE_H.set("The game has ended!");
        GE_C.set("An event has led the game to finish");
        CNT_SG_H.set("You can't start the game yet!");
        CNT_SG_C1.set("All players must be ready to start the game");
        CNT_SG_C2.set("Please wait for your opponent to join you");
        READY.set("Ready");
    }

    public static void first_timer() {
        String lang = System.getProperty("user.language");
        switch (lang) {
            case "fr":
                load_lang(LANGNAME.FRENCH);
                break;
            case "ar":
                load_lang(LANGNAME.ARABIC);
                break;
            default:
                load_lang(LANGNAME.ENGLISH);
        }
    }

}
