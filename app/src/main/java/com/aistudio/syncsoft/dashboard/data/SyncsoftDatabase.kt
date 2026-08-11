package com.aistudio.syncsoft.dashboard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProjectEntity::class,
        TaskEntity::class,
        CalendarEventEntity::class,
        ChatMessageEntity::class,
        ActivityLogEntity::class,
        AutomationRuleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SyncsoftDatabase : RoomDatabase() {
    abstract fun syncsoftDao(): SyncsoftDao

    companion object {
        @Volatile
        private var INSTANCE: SyncsoftDatabase? = null

        fun getDatabase(context: Context): SyncsoftDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SyncsoftDatabase::class.java,
                    "syncsoft_dashboard_db"
                )
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.syncsoftDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: SyncsoftDao) {
                // Populate Initial Projects
                val defaultProjects = listOf(
                    ProjectEntity(
                        id = "proj-1",
                        name = "Residencial Solar - Familia Pérez",
                        location = "Col. Centro, GDL",
                        stage = "NUEVA_VENTA",
                        status = "ACTIVE",
                        salePrice = 85000.0,
                        clientName = "Juan Pérez",
                        colorHex = "#4F46E5"
                    ),
                    ProjectEntity(
                        id = "proj-2",
                        name = "Sistema Bombeo Solar - Rancho El Sol",
                        location = "Zapopan, JAL",
                        stage = "INSTALACION",
                        status = "ACTIVE",
                        salePrice = 120000.0,
                        clientName = "María González",
                        colorHex = "#EC4899"
                    ),
                    ProjectEntity(
                        id = "proj-3",
                        name = "Comercial - Mueblerías Del Valle",
                        location = "Tlaquepaque, JAL",
                        stage = "TRAMITE_CFE",
                        status = "ACTIVE",
                        salePrice = 450000.0,
                        clientName = "Mueblerías Del Valle S.A.",
                        colorHex = "#F59E0B"
                    ),
                    ProjectEntity(
                        id = "proj-4",
                        name = "Industrial - Empaques Nacionales",
                        location = "Parque Industrial, Tlajomulco",
                        stage = "FINALIZADO",
                        status = "COMPLETED",
                        salePrice = 1250000.0,
                        clientName = "Empaques Nacionales",
                        colorHex = "#06B6D4"
                    )
                )
                dao.insertProjects(defaultProjects)

                // Populate Initial Tasks
                val defaultTasks = listOf(
                    TaskEntity(
                        projectId = "proj-1",
                        projectTitle = "Syncsoft Core Dashboard v3.0",
                        title = "Implement Automated Status Progression Engine",
                        description = "Auto-advance tasks to 'In Review' upon GitHub pull request merge.",
                        department = "Engineering",
                        assigneeName = "Alex Rivera",
                        assigneeRole = "Senior Fullstack Eng",
                        status = "IN_PROGRESS",
                        priority = "HIGH",
                        dueDate = "Today, 5:00 PM",
                        isAutomated = true,
                        automationTrigger = "GitHub PR Merge -> Auto Move"
                    ),
                    TaskEntity(
                        projectId = "proj-1",
                        projectTitle = "Syncsoft Core Dashboard v3.0",
                        title = "Real-time Presence WebSocket Integration",
                        description = "Connect live presence indicator badge with user active state.",
                        department = "Engineering",
                        assigneeName = "Sarah Jenkins",
                        assigneeRole = "Tech Lead",
                        status = "IN_REVIEW",
                        priority = "URGENT",
                        dueDate = "Tomorrow",
                        isAutomated = false
                    ),
                    TaskEntity(
                        projectId = "proj-2",
                        projectTitle = "Brand Guidelines & UI System Sync",
                        title = "Audit M3 Dark Mode Palette Tokens",
                        description = "Ensure high-contrast accessible color contrast ratios across all components.",
                        department = "Design",
                        assigneeName = "Marcus Chen",
                        assigneeRole = "Principal Designer",
                        status = "COMPLETED",
                        priority = "MEDIUM",
                        dueDate = "Jul 24",
                        isAutomated = true,
                        automationTrigger = "Auto Figma Token Import"
                    ),
                    TaskEntity(
                        projectId = "proj-3",
                        projectTitle = "Q3 Global Enterprise Campaign",
                        title = "Automated Calendar Sync for Product Webinars",
                        description = "Integrate Syncsoft calendar with Google Calendar & Outlook API.",
                        department = "Marketing",
                        assigneeName = "Elena Rostova",
                        assigneeRole = "Growth Marketing Director",
                        status = "TODO",
                        priority = "HIGH",
                        dueDate = "Aug 02",
                        isAutomated = true,
                        automationTrigger = "Auto Schedule Attendees"
                    ),
                    TaskEntity(
                        projectId = "proj-4",
                        projectTitle = "Automated CI/CD Deployment Pipeline",
                        title = "Configure Automated Slack & Syncsoft Channel Alerts",
                        description = "Trigger real-time alert card when build status changes.",
                        department = "Operations",
                        assigneeName = "David Kim",
                        assigneeRole = "DevOps Lead",
                        status = "IN_PROGRESS",
                        priority = "MEDIUM",
                        dueDate = "Aug 05",
                        isAutomated = true
                    )
                )
                dao.insertTasks(defaultTasks)

                // Populate Calendar Events
                val defaultCalendarEvents = listOf(
                    CalendarEventEntity(
                        title = "Cross-Department Daily Standup",
                        description = "Synchronize sprint velocity, review automated task bottlenecks.",
                        department = "All Departments",
                        startTime = "09:30 AM",
                        endTime = "09:45 AM",
                        dateString = "2026-07-26",
                        eventType = "MEETING",
                        meetingUrl = "https://syncsoft.app/huddle/standup-daily",
                        attendeesCsv = "Sarah, Alex, Marcus, Elena, David",
                        syncPlatform = "Syncsoft + Google Calendar"
                    ),
                    CalendarEventEntity(
                        title = "Automated Sprint Review & Demo",
                        description = "AI generated sprint summary presentation and task auto-archiving.",
                        department = "Engineering",
                        startTime = "02:00 PM",
                        endTime = "03:00 PM",
                        dateString = "2026-07-26",
                        eventType = "AUTOMATED_REVIEW",
                        meetingUrl = "https://syncsoft.app/huddle/sprint-review",
                        attendeesCsv = "Engineering Team",
                        syncPlatform = "Syncsoft + Outlook"
                    ),
                    CalendarEventEntity(
                        title = "Q3 Enterprise Product Launch Milestone",
                        description = "Global deployment go-live deadline across all regions.",
                        department = "Marketing",
                        startTime = "05:00 PM",
                        endTime = "06:00 PM",
                        dateString = "2026-07-28",
                        eventType = "MILESTONE",
                        meetingUrl = "",
                        attendeesCsv = "Executive Board, All Team Leads",
                        syncPlatform = "Syncsoft Platform"
                    )
                )
                dao.insertCalendarEvents(defaultCalendarEvents)

                // Populate Chat Messages
                val defaultMessages = listOf(
                    ChatMessageEntity(
                        channelId = "general",
                        senderName = "Syncsoft Bot (Automated)",
                        senderRole = "Workflow AI",
                        messageText = "⚡ Task #101 'Implement Automated Status Progression' moved to [IN_PROGRESS] automatically via Git PR #402.",
                        timestamp = System.currentTimeMillis() - 3600000,
                        linkedTaskId = 1,
                        linkedTaskTitle = "Implement Automated Status Progression Engine"
                    ),
                    ChatMessageEntity(
                        channelId = "general",
                        senderName = "Sarah Jenkins",
                        senderRole = "Tech Lead",
                        messageText = "Great work Alex! Design tokens look sharp on the new build.",
                        timestamp = System.currentTimeMillis() - 1800000
                    ),
                    ChatMessageEntity(
                        channelId = "engineering",
                        senderName = "Alex Rivera",
                        senderRole = "Senior Fullstack Eng",
                        messageText = "Calendar sync hook is working seamlessly with Google Calendar and Outlook APIs!",
                        timestamp = System.currentTimeMillis() - 900000
                    )
                )
                dao.insertChatMessages(defaultMessages)

                // Populate Activity Logs
                val defaultLogs = listOf(
                    ActivityLogEntity(
                        userName = "Syncsoft Bot",
                        action = "Auto-triggered task transition",
                        target = "Implement Automated Status Progression Engine",
                        department = "Engineering",
                        timestampFormatted = "10 mins ago",
                        activityType = "AUTOMATION"
                    ),
                    ActivityLogEntity(
                        userName = "Marcus Chen",
                        action = "Completed design review audit",
                        target = "Audit M3 Dark Mode Palette Tokens",
                        department = "Design",
                        timestampFormatted = "25 mins ago",
                        activityType = "TASK_UPDATE"
                    ),
                    ActivityLogEntity(
                        userName = "Elena Rostova",
                        action = "Synced event with Google Calendar",
                        target = "Automated Calendar Sync for Product Webinars",
                        department = "Marketing",
                        timestampFormatted = "1 hr ago",
                        activityType = "CALENDAR_SYNC"
                    )
                )
                dao.insertActivityLogs(defaultLogs)

                // Populate Automation Rules
                val defaultRules = listOf(
                    AutomationRuleEntity(
                        title = "Auto-Advance Task on Git PR Merge",
                        department = "Engineering",
                        triggerEvent = "When a GitHub Pull Request is merged into main",
                        actionResult = "Move task to 'In Review' & notify channel #engineering",
                        isEnabled = true,
                        runCount = 142
                    ),
                    AutomationRuleEntity(
                        title = "Automated Calendar Review Scheduling",
                        department = "All Departments",
                        triggerEvent = "When task marked Urgent or High Priority",
                        actionResult = "Schedule 15-min sync event in Google/Outlook Calendar",
                        isEnabled = true,
                        runCount = 89
                    ),
                    AutomationRuleEntity(
                        title = "AI Weekly Workload & Bottleneck Audit",
                        department = "Product",
                        triggerEvent = "Every Friday at 4:00 PM",
                        actionResult = "Generate executive summary report and re-assign blocked tasks",
                        isEnabled = true,
                        runCount = 28
                    )
                )
                dao.insertAutomationRules(defaultRules)
            }
        }
    }
}
