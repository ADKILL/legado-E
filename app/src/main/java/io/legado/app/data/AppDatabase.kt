package io.legado.app.data

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.legado.app.data.dao.BookChapterDao
import io.legado.app.data.dao.BookDao
import io.legado.app.data.dao.BookGroupDao
import io.legado.app.data.dao.BookSourceDao
import io.legado.app.data.dao.BookmarkDao
import io.legado.app.data.dao.CacheDao
import io.legado.app.data.dao.CookieDao
import io.legado.app.data.dao.DictRuleDao
import io.legado.app.data.dao.HttpTTSDao
import io.legado.app.data.dao.KeyboardAssistsDao
import io.legado.app.data.dao.ReadRecordDao
import io.legado.app.data.dao.ReplaceRuleDao
import io.legado.app.data.dao.RssArticleDao
import io.legado.app.data.dao.RssReadRecordDao
import io.legado.app.data.dao.RssSourceDao
import io.legado.app.data.dao.RssStarDao
import io.legado.app.data.dao.RuleSubDao
import io.legado.app.data.dao.SearchBookDao
import io.legado.app.data.dao.SearchKeywordDao
import io.legado.app.data.dao.ServerDao
import io.legado.app.data.dao.TxtTocRuleDao
import io.legado.app.data.entities.Book
import io.legado.app.data.entities.BookChapter
import io.legado.app.data.entities.BookGroup
import io.legado.app.data.entities.BookSource
import io.legado.app.data.entities.BookSourcePart
import io.legado.app.data.entities.Bookmark
import io.legado.app.data.entities.Cache
import io.legado.app.data.entities.Cookie
import io.legado.app.data.entities.DictRule
import io.legado.app.data.entities.HttpTTS
import io.legado.app.data.entities.KeyboardAssist
import io.legado.app.data.entities.ReadRecord
import io.legado.app.data.entities.ReplaceRule
import io.legado.app.data.entities.RssArticle
import io.legado.app.data.entities.RssReadRecord
import io.legado.app.data.entities.RssSource
import io.legado.app.data.entities.RssStar
import io.legado.app.data.entities.RuleSub
import io.legado.app.data.entities.SearchBook
import io.legado.app.data.entities.SearchKeyword
import io.legado.app.data.entities.Server
import io.legado.app.data.entities.TxtTocRule
import io.legado.app.help.DefaultData
import org.intellij.lang.annotations.Language
import splitties.init.appCtx
import java.io.File
import java.util.Locale


val appDb by lazy {
    /*
     * 在创建 Room 之前先确认外部存储权限。
     *
     * Android 11+ 如果没有 MANAGE_EXTERNAL_STORAGE，
     * 不允许 Room 创建或打开数据库。
     *
     * 这样可以避免因为权限不足而错误地回退到内部数据库。
     */
    AppDatabase.ensureExternalStorageAccess()

    /*
     * 第一次启动修改版时：
     *
     * 如果外部数据库不存在，而旧的内部数据库存在，
     * 则先把旧数据库迁移到 /storage/emulated/0/LegadoDB/
     */
    AppDatabase.prepareExternalDatabase()

    Room.databaseBuilder(
        appCtx,
        AppDatabase::class.java,
        AppDatabase.DATABASE_PATH
    )
        .fallbackToDestructiveMigrationFrom(false, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        .addMigrations(*DatabaseMigrations.migrations)
        .allowMainThreadQueries()
        .addCallback(AppDatabase.dbCallback)
        .build()
}


@Database(
    version = 89,
    exportSchema = true,
    entities = [
        Book::class,
        BookGroup::class,
        BookSource::class,
        BookChapter::class,
        ReplaceRule::class,
        SearchBook::class,
        SearchKeyword::class,
        Cookie::class,
        RssSource::class,
        Bookmark::class,
        RssArticle::class,
        RssReadRecord::class,
        RssStar::class,
        TxtTocRule::class,
        ReadRecord::class,
        HttpTTS::class,
        Cache::class,
        RuleSub::class,
        DictRule::class,
        KeyboardAssist::class,
        Server::class
    ],
    views = [BookSourcePart::class],
    autoMigrations = [
        AutoMigration(from = 43, to = 44),
        AutoMigration(from = 44, to = 45),
        AutoMigration(from = 45, to = 46),
        AutoMigration(from = 46, to = 47),
        AutoMigration(from = 47, to = 48),
        AutoMigration(from = 48, to = 49),
        AutoMigration(from = 49, to = 50),
        AutoMigration(from = 50, to = 51),
        AutoMigration(from = 51, to = 52),
        AutoMigration(from = 52, to = 53),
        AutoMigration(from = 53, to = 54),
        AutoMigration(from = 54, to = 55, spec = DatabaseMigrations.Migration_54_55::class),
        AutoMigration(from = 55, to = 56),
        AutoMigration(from = 56, to = 57),
        AutoMigration(from = 57, to = 58),
        AutoMigration(from = 58, to = 59),
        AutoMigration(from = 59, to = 60),
        AutoMigration(from = 60, to = 61),
        AutoMigration(from = 61, to = 62),
        AutoMigration(from = 62, to = 63),
        AutoMigration(from = 63, to = 64),
        AutoMigration(from = 64, to = 65, spec = DatabaseMigrations.Migration_64_65::class),
        AutoMigration(from = 65, to = 66),
        AutoMigration(from = 66, to = 67),
        AutoMigration(from = 67, to = 68),
        AutoMigration(from = 68, to = 69),
        AutoMigration(from = 69, to = 70),
        AutoMigration(from = 70, to = 71),
        AutoMigration(from = 71, to = 72),
        AutoMigration(from = 72, to = 73),
        AutoMigration(from = 73, to = 74),
        AutoMigration(from = 74, to = 75),
        AutoMigration(from = 75, to = 76),
        AutoMigration(from = 76, to = 77),
        AutoMigration(from = 77, to = 78),
        AutoMigration(from = 78, to = 79),
        AutoMigration(from = 79, to = 80),
        AutoMigration(from = 80, to = 81, spec = DatabaseMigrations.Migration_80_81::class),
        AutoMigration(from = 81, to = 82),
        AutoMigration(from = 82, to = 83),
        AutoMigration(from = 83, to = 84, spec = DatabaseMigrations.Migration_83_84::class),
        AutoMigration(from = 84, to = 85, spec = DatabaseMigrations.Migration_84_85::class),
        AutoMigration(from = 85, to = 86),
        AutoMigration(from = 86, to = 87),
        AutoMigration(from = 87, to = 88),
        AutoMigration(from = 88, to = 89)
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract val bookDao: BookDao
    abstract val bookGroupDao: BookGroupDao
    abstract val bookSourceDao: BookSourceDao
    abstract val bookChapterDao: BookChapterDao
    abstract val replaceRuleDao: ReplaceRuleDao
    abstract val searchBookDao: SearchBookDao
    abstract val searchKeywordDao: SearchKeywordDao
    abstract val rssSourceDao: RssSourceDao
    abstract val bookmarkDao: BookmarkDao
    abstract val rssArticleDao: RssArticleDao
    abstract val rssStarDao: RssStarDao
    abstract val rssReadRecordDao: RssReadRecordDao
    abstract val cookieDao: CookieDao
    abstract val txtTocRuleDao: TxtTocRuleDao
    abstract val readRecordDao: ReadRecordDao
    abstract val httpTTSDao: HttpTTSDao
    abstract val cacheDao: CacheDao
    abstract val ruleSubDao: RuleSubDao
    abstract val dictRuleDao: DictRuleDao
    abstract val keyboardAssistsDao: KeyboardAssistsDao
    abstract val serverDao: ServerDao

    companion object {

        /**
         * 原来的数据库文件名。
         *
         * 内部数据库：
         * /data/data/io.legado.app/databases/legado.db
         */
        const val DATABASE_NAME = "legado.db"

        /**
         * 新的外部数据库目录。
         *
         * 最终文件：
         * /storage/emulated/0/LegadoDB/legado.db
         */
        private val DATABASE_DIR = File(
            Environment.getExternalStorageDirectory(),
            "LegadoDB"
        )

        /**
         * Room 使用的数据库绝对路径。
         *
         * Room 官方支持将绝对路径作为 databaseBuilder 的 name。
         */
        val DATABASE_PATH = File(
            DATABASE_DIR,
            DATABASE_NAME
        ).absolutePath

        const val BOOK_TABLE_NAME = "books"
        const val BOOK_SOURCE_TABLE_NAME = "book_sources"
        const val RSS_SOURCE_TABLE_NAME = "rssSources"


        /**
         * 检查 Android 11+ 的 All Files Access。
         *
         * 如果没有权限：
         *
         * 1. 不允许 Room 创建数据库
         * 2. 尝试打开系统权限设置页面
         * 3. 抛出明确异常
         *
         * 这样不会因为权限不足而偷偷创建一个错误的内部数据库。
         */
        fun ensureExternalStorageAccess() {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return
            }

            if (Environment.isExternalStorageManager()) {
                return
            }

            Log.e(
                "AppDatabase",
                "没有 MANAGE_EXTERNAL_STORAGE 权限，无法打开外部数据库：$DATABASE_PATH"
            )

            /*
             * 尝试直接打开当前应用的“所有文件访问权限”页面。
             */
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:${appCtx.packageName}")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                appCtx.startActivity(intent)

            } catch (e: Exception) {

                Log.e(
                    "AppDatabase",
                    "无法打开应用的所有文件访问权限页面，尝试打开通用设置页面",
                    e
                )

                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    appCtx.startActivity(intent)

                } catch (e2: Exception) {

                    Log.e(
                        "AppDatabase",
                        "无法打开所有文件访问权限设置页面",
                        e2
                    )
                }
            }

            throw IllegalStateException(
                "Legado 数据库需要“所有文件访问权限”。\n" +
                    "请在系统设置中允许 Legado 管理所有文件后重新启动应用。\n" +
                    "数据库位置：$DATABASE_PATH"
            )
        }


        /**
         * 将原来的内部数据库迁移到外部存储。
         *
         * 原数据库：
         * /data/data/io.legado.app/databases/legado.db
         *
         * 新数据库：
         * /storage/emulated/0/LegadoDB/legado.db
         *
         * 注意：
         * 这里不会删除旧数据库。
         */
        fun prepareExternalDatabase() {

            val externalDb = File(DATABASE_PATH)

            /*
             * 如果外部数据库已经存在，
             * 说明已经完成迁移，不再重复复制。
             */
            if (externalDb.exists()) {
                Log.d(
                    "AppDatabase",
                    "外部数据库已经存在：${externalDb.absolutePath}"
                )
                return
            }

            /*
             * 创建：
             *
             * /storage/emulated/0/LegadoDB/
             */
            if (!DATABASE_DIR.exists()) {
                if (!DATABASE_DIR.mkdirs() && !DATABASE_DIR.exists()) {
                    throw IllegalStateException(
                        "无法创建数据库目录：${DATABASE_DIR.absolutePath}"
                    )
                }
            }

            /*
             * 原来的内部数据库。
             */
            val oldDb = appCtx.getDatabasePath(DATABASE_NAME)

            /*
             * 如果旧数据库不存在，
             * 就不需要迁移。
             *
             * Room 后面会直接创建新的外部数据库。
             */
            if (!oldDb.exists()) {
                Log.d(
                    "AppDatabase",
                    "没有发现旧的内部数据库，将直接创建新的外部数据库：$DATABASE_PATH"
                )
                return
            }

            Log.i(
                "AppDatabase",
                "发现旧数据库，开始迁移：${oldDb.absolutePath} -> $DATABASE_PATH"
            )

            try {

                /*
                 * 复制主数据库文件。
                 */
                oldDb.copyTo(
                    externalDb,
                    overwrite = false
                )

                /*
                 * SQLite WAL 文件。
                 *
                 * 如果存在，必须一起复制。
                 */
                val oldWal = File("${oldDb.absolutePath}-wal")
                val externalWal = File("${externalDb.absolutePath}-wal")

                if (oldWal.exists()) {
                    oldWal.copyTo(
                        externalWal,
                        overwrite = false
                    )

                    Log.i(
                        "AppDatabase",
                        "已迁移 WAL 文件：${oldWal.absolutePath}"
                    )
                }

                /*
                 * SQLite SHM 文件。
                 *
                 * 如果存在也复制过去。
                 */
                val oldShm = File("${oldDb.absolutePath}-shm")
                val externalShm = File("${externalDb.absolutePath}-shm")

                if (oldShm.exists()) {
                    oldShm.copyTo(
                        externalShm,
                        overwrite = false
                    )

                    Log.i(
                        "AppDatabase",
                        "已迁移 SHM 文件：${oldShm.absolutePath}"
                    )
                }

                Log.i(
                    "AppDatabase",
                    "数据库迁移成功：${externalDb.absolutePath}"
                )

            } catch (e: Exception) {

                /*
                 * 如果迁移失败，为避免留下一个不完整的数据库，
                 * 删除已经复制出来的外部文件。
                 *
                 * 原来的内部数据库完全不动。
                 */
                try {
                    externalDb.delete()
                    File("${externalDb.absolutePath}-wal").delete()
                    File("${externalDb.absolutePath}-shm").delete()
                } catch (cleanupException: Exception) {
                    Log.e(
                        "AppDatabase",
                        "清理失败的外部数据库文件时发生错误",
                        cleanupException
                    )
                }

                Log.e(
                    "AppDatabase",
                    "数据库迁移失败，保留原内部数据库",
                    e
                )

                throw IllegalStateException(
                    "Legado 数据库迁移失败。\n" +
                        "原数据库仍然保留：${oldDb.absolutePath}\n" +
                        "目标位置：$DATABASE_PATH",
                    e
                )
            }
        }


        val dbCallback = object : Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {

                // 只在 API 级别 23 (Marshmallow) 及以上版本尝试设置区域设置
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        Log.d(
                            "AppDatabaseCallback",
                            "准备 设置 locale for API ${Build.VERSION.SDK_INT}..."
                        )

                        db.setLocale(Locale.CHINESE)

                        // 在 21 上报错，但无法拦截
                        Log.d(
                            "AppDatabaseCallback",
                            "成功 设置 locale for API ${Build.VERSION.SDK_INT}."
                        )

                    } catch (e: Exception) {

                        Log.e(
                            "AppDatabaseCallback",
                            "错误 设置 locale in onCreate for API ${Build.VERSION.SDK_INT}",
                            e
                        )
                    }

                } else {

                    Log.i(
                        "AppDatabaseCallback",
                        "跳过 setLocale for API ${Build.VERSION.SDK_INT} (below M)."
                    )
                }
            }


            override fun onOpen(db: SupportSQLiteDatabase) {

                @Language("sql")
                val insertBookGroupAllSql = """
                    insert into book_groups(groupId, groupName, 'order', show) 
                    select ${BookGroup.IdAll}, '全部', -10, 1
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdAll})
                """.trimIndent()

                db.execSQL(insertBookGroupAllSql)


                @Language("sql")
                val insertBookGroupLocalSql = """
                    insert into book_groups(groupId, groupName, 'order', enableRefresh, show) 
                    select ${BookGroup.IdLocal}, '本地', -9, 0, 1
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdLocal})
                """.trimIndent()

                db.execSQL(insertBookGroupLocalSql)


                @Language("sql")
                val insertBookGroupMusicSql = """
                    insert into book_groups(groupId, groupName, 'order', show) 
                    select ${BookGroup.IdAudio}, '音频', -8, 1
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdAudio})
                """.trimIndent()

                db.execSQL(insertBookGroupMusicSql)


                @Language("sql")
                val insertBookGroupNetNoneGroupSql = """
                    insert into book_groups(groupId, groupName, 'order', show) 
                    select ${BookGroup.IdNetNone}, '网络未分组', -7, 1
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdNetNone})
                """.trimIndent()

                db.execSQL(insertBookGroupNetNoneGroupSql)


                @Language("sql")
                val insertBookGroupLocalNoneGroupSql = """
                    insert into book_groups(groupId, groupName, 'order', show) 
                    select ${BookGroup.IdLocalNone}, '本地未分组', -6, 0
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdLocalNone})
                """.trimIndent()

                db.execSQL(insertBookGroupLocalNoneGroupSql)


                @Language("sql")
                val insertBookGroupVideoSql = """
                    insert into book_groups(groupId, groupName, 'order', show) 
                    select ${BookGroup.IdVideo}, '视频', -5, 1
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdVideo})
                    """.trimIndent()

                db.execSQL(insertBookGroupVideoSql)


                @Language("sql")
                val insertBookGroupErrorSql = """
                    insert into book_groups(groupId, groupName, 'order', show) 
                    select ${BookGroup.IdError}, '更新失败', -1, 1
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdError})
                """.trimIndent()

                db.execSQL(insertBookGroupErrorSql)


                @Language("sql")
                val upBookSourceLoginUiSql =
                    "update book_sources set loginUi = null where loginUi = 'null'"

                db.execSQL(upBookSourceLoginUiSql)


                @Language("sql")
                val upRssSourceLoginUiSql =
                    "update rssSources set loginUi = null where loginUi = 'null'"

                db.execSQL(upRssSourceLoginUiSql)


                @Language("sql")
                val upHttpTtsLoginUiSql =
                    "update httpTTS set loginUi = null where loginUi = 'null'"

                db.execSQL(upHttpTtsLoginUiSql)


                @Language("sql")
                val upHttpTtsConcurrentRateSql =
                    "update httpTTS set concurrentRate = '0' where concurrentRate is null"

                db.execSQL(upHttpTtsConcurrentRateSql)


                db.query(
                    "select * from keyboardAssists order by serialNo"
                ).use {

                    if (it.count == 0) {

                        DefaultData.keyboardAssists.forEach { keyboardAssist ->

                            val contentValues = ContentValues().apply {
                                put("type", keyboardAssist.type)
                                put("key", keyboardAssist.key)
                                put("value", keyboardAssist.value)
                                put("serialNo", keyboardAssist.serialNo)
                            }

                            db.insert(
                                "keyboardAssists",
                                SQLiteDatabase.CONFLICT_REPLACE,
                                contentValues
                            )
                        }
                    }
                }
            }
        }
    }
}
