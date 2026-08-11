package com.aistudio.syncsoft.dashboard.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SyncsoftRepository(private val dao: SyncsoftDao) {

    val allProjects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val allCalendarEvents: Flow<List<CalendarEventEntity>> = dao.getAllCalendarEvents()
    val activityLogs: Flow<List<ActivityLogEntity>> = dao.getActivityLogs()
    val automationRules: Flow<List<AutomationRuleEntity>> = dao.getAutomationRules()

    // Real-Time Team Presence Simulation
    private val _teamPresence = MutableStateFlow(
        listOf(
            TeamMemberPresence("1", "Sarah Jenkins", "Tech Lead", "Engineering", "In Standup", true, "Syncsoft Core Dashboard v3.0"),
            TeamMemberPresence("2", "Alex Rivera", "Senior Fullstack Eng", "Engineering", "Coding", true, "Automated Progression Engine"),
            TeamMemberPresence("3", "Marcus Chen", "Principal Designer", "Design", "In Review", true, "M3 Token System"),
            TeamMemberPresence("4", "Elena Rostova", "Growth Director", "Marketing", "Deep Work", true, "Q3 Global Campaign"),
            TeamMemberPresence("5", "David Kim", "DevOps Lead", "Operations", "Available", true, "CI/CD Deployment Pipeline"),
            TeamMemberPresence("6", "Jessica Wong", "VP Product", "Product", "In Meeting", true, "Roadmap Prioritization")
        )
    )
    val teamPresence: StateFlow<List<TeamMemberPresence>> = _teamPresence.asStateFlow()

    fun getTasksByDepartment(dept: String): Flow<List<TaskEntity>> = dao.getTasksByDepartment(dept)

    fun getChatMessages(channelId: String): Flow<List<ChatMessageEntity>> = dao.getChatMessagesForChannel(channelId)

    suspend fun addTask(
        projectId: String,
        projectTitle: String,
        title: String,
        description: String,
        department: String,
        assigneeName: String,
        priority: String,
        dueDate: String,
        isAutomated: Boolean,
        automationTrigger: String = ""
    ): Long {
        val newTask = TaskEntity(
            projectId = projectId,
            projectTitle = projectTitle,
            title = title,
            description = description,
            department = department,
            assigneeName = assigneeName,
            assigneeRole = "Team Member",
            status = "TODO",
            priority = priority,
            dueDate = dueDate,
            isAutomated = isAutomated,
            automationTrigger = automationTrigger
        )
        val taskId = dao.insertTask(newTask)

        // Log Activity
        val log = ActivityLogEntity(
            userName = assigneeName,
            action = if (isAutomated) "Automated workflow generated task" else "Created task",
            target = title,
            department = department,
            timestampFormatted = "Just now",
            activityType = if (isAutomated) "AUTOMATION" else "TASK_UPDATE"
        )
        dao.insertActivityLog(log)

        // Send Chat Announcement
        val chatMsg = ChatMessageEntity(
            channelId = department.lowercase(Locale.ROOT),
            senderName = "Syncsoft Automation",
            senderRole = "Bot",
            messageText = "📌 New task added: '$title' assigned to $assigneeName. Priority: $priority.",
            linkedTaskId = taskId,
            linkedTaskTitle = title
        )
        dao.insertChatMessage(chatMsg)

        return taskId
    }

    suspend fun updateTaskStatus(taskId: Long, newStatus: String, taskTitle: String, department: String) {
        dao.updateTaskStatus(taskId, newStatus)

        // Add activity log
        val log = ActivityLogEntity(
            userName = "Alex Rivera",
            action = "Updated status to [${TaskStatus.values().find { it.name == newStatus }?.displayName ?: newStatus}]",
            target = taskTitle,
            department = department,
            timestampFormatted = "Just now",
            activityType = "TASK_UPDATE"
        )
        dao.insertActivityLog(log)

        // Auto trigger chat notification
        val msg = ChatMessageEntity(
            channelId = "general",
            senderName = "Syncsoft Bot",
            senderRole = "Automated Engine",
            messageText = "⚡ Task '$taskTitle' moved to [${TaskStatus.values().find { it.name == newStatus }?.displayName ?: newStatus}].",
            linkedTaskId = taskId,
            linkedTaskTitle = taskTitle
        )
        dao.insertChatMessage(msg)

        // Auto schedule review meeting if moved to IN_REVIEW
        if (newStatus == "IN_REVIEW") {
            val reviewEvent = CalendarEventEntity(
                title = "Automated Review: $taskTitle",
                description = "Cross-department review session generated automatically by Syncsoft trigger.",
                department = department,
                startTime = "04:00 PM",
                endTime = "04:30 PM",
                dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                eventType = "AUTOMATED_REVIEW",
                meetingUrl = "https://syncsoft.app/huddle/review-$taskId",
                attendeesCsv = "Lead Reviewers, $department Team",
                syncPlatform = "Google Calendar & Outlook"
            )
            dao.insertCalendarEvent(reviewEvent)

            val calendarLog = ActivityLogEntity(
                userName = "Syncsoft Calendar Sync",
                action = "Auto-scheduled review event in Google/Outlook",
                target = "Automated Review: $taskTitle",
                department = department,
                timestampFormatted = "Just now",
                activityType = "CALENDAR_SYNC"
            )
            dao.insertActivityLog(calendarLog)
        }
    }

    suspend fun addCalendarEvent(
        title: String,
        description: String,
        department: String,
        startTime: String,
        endTime: String,
        dateString: String,
        eventType: String,
        attendeesCsv: String,
        syncPlatform: String
    ): Long {
        val event = CalendarEventEntity(
            title = title,
            description = description,
            department = department,
            startTime = startTime,
            endTime = endTime,
            dateString = dateString,
            eventType = eventType,
            meetingUrl = "https://syncsoft.app/huddle/${title.lowercase().replace(" ", "-")}",
            attendeesCsv = attendeesCsv,
            syncPlatform = syncPlatform
        )
        val eventId = dao.insertCalendarEvent(event)

        val log = ActivityLogEntity(
            userName = "Sarah Jenkins",
            action = "Scheduled meeting & synced with $syncPlatform",
            target = title,
            department = department,
            timestampFormatted = "Just now",
            activityType = "CALENDAR_SYNC"
        )
        dao.insertActivityLog(log)

        return eventId
    }

    suspend fun sendChatMessage(channelId: String, senderName: String, messageText: String, linkedTaskId: Long? = null, linkedTaskTitle: String? = null) {
        val msg = ChatMessageEntity(
            channelId = channelId,
            senderName = senderName,
            senderRole = "Team Member",
            messageText = messageText,
            timestamp = System.currentTimeMillis(),
            linkedTaskId = linkedTaskId,
            linkedTaskTitle = linkedTaskTitle
        )
        dao.insertChatMessage(msg)
    }

    suspend fun toggleAutomationRule(ruleId: Long, isEnabled: Boolean) {
        dao.toggleAutomationRule(ruleId, isEnabled)
    }

    suspend fun runAIAutoBreakdown(projectTitle: String, taskPrompt: String, department: String) {
        val subtasks = GeminiClient.generateTaskBreakdown(projectTitle, taskPrompt, department)
        val p = dao.getAllProjects()
        for ((title, priority) in subtasks) {
            addTask(
                projectId = "proj-1",
                projectTitle = projectTitle,
                title = title,
                description = "AI auto-generated subtask based on request: $taskPrompt",
                department = department,
                assigneeName = "Syncsoft AI Auto-Assign",
                priority = priority,
                dueDate = "Tomorrow",
                isAutomated = true,
                automationTrigger = "Gemini AI Smart Task Breakdown"
            )
        }
    }

    suspend fun generateStandupReport(tasks: List<TaskEntity>, department: String): String {
        return GeminiClient.generateExecutiveStandupReport(tasks, department)
    }

    suspend fun addAutomationRule(title: String, department: String, triggerEvent: String, actionResult: String): Long {
        val rule = AutomationRuleEntity(
            title = title,
            department = department,
            triggerEvent = triggerEvent,
            actionResult = actionResult,
            isEnabled = true,
            runCount = 1
        )
        val id = dao.insertAutomationRule(rule)

        val log = ActivityLogEntity(
            userName = "Alex Rivera",
            action = "Configured new automation rule",
            target = title,
            department = department,
            timestampFormatted = "Just now",
            activityType = "AUTOMATION"
        )
        dao.insertActivityLog(log)
        return id
    }

    suspend fun updateProjectStage(projectId: String, newStage: String) {
        dao.updateProjectStage(projectId, newStage)
        val log = ActivityLogEntity(
            userName = "Alex Rivera",
            action = "Updated project stage to $newStage",
            target = "Project #$projectId",
            department = "Engineering",
            timestampFormatted = "Just now",
            activityType = "TASK_UPDATE"
        )
        dao.insertActivityLog(log)
    }

    suspend fun refreshProjects() {
        try {
            val response = com.aistudio.syncsoft.dashboard.network.SyncSoftApiClient.apiService.getProjects()
            if (response.isSuccessful) {
                response.body()?.let { projects ->
                    // clear or update? Insert with replace is defined in DAO.
                    dao.insertProjects(projects)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
