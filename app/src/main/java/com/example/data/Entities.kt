package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Department(val displayName: String, val colorHex: String) {
    ALL("All Departments", "#4F46E5"),
    ENGINEERING("Engineering", "#4F46E5"),
    DESIGN("Design", "#EC4899"),
    MARKETING("Marketing", "#F59E0B"),
    PRODUCT("Product", "#8B5CF6"),
    OPERATIONS("Operations", "#06B6D4"),
    HR("Human Resources", "#10B981")
}

enum class TaskStatus(val displayName: String) {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    IN_REVIEW("In Review"),
    COMPLETED("Completed")
}

enum class TaskPriority(val displayName: String, val colorHex: String) {
    LOW("Low", "#10B981"),
    MEDIUM("Medium", "#3B82F6"),
    HIGH("High", "#F59E0B"),
    URGENT("Urgent", "#EF4444")
}

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val department: String,
    val description: String,
    val progressPercentage: Int, // 0 - 100
    val status: String, // e.g., "Active", "Planning", "Completed"
    val teamLeadName: String,
    val memberCount: Int,
    val dueDate: String,
    val colorHex: String
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val projectTitle: String,
    val title: String,
    val description: String,
    val department: String,
    val assigneeName: String,
    val assigneeRole: String,
    val status: String, // TODO, IN_PROGRESS, IN_REVIEW, COMPLETED
    val priority: String, // LOW, MEDIUM, HIGH, URGENT
    val dueDate: String,
    val isAutomated: Boolean = false,
    val automationTrigger: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val department: String,
    val startTime: String, // e.g. "10:00 AM"
    val endTime: String,   // e.g. "11:00 AM"
    val dateString: String, // e.g. "2026-07-26"
    val eventType: String, // MEETING, MILESTONE, AUTOMATED_REVIEW, DEADLINE
    val meetingUrl: String = "",
    val attendeesCsv: String = "",
    val syncPlatform: String = "Syncsoft + Google Calendar"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val channelId: String, // e.g. "general", "engineering", "design-system"
    val senderName: String,
    val senderRole: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val linkedTaskId: Long? = null,
    val linkedTaskTitle: String? = null,
    val isHuddleNotification: Boolean = false
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userName: String,
    val action: String, // e.g. "Auto-moved task to In Review", "Scheduled Sprint Sync"
    val target: String,
    val department: String,
    val timestampFormatted: String,
    val activityType: String // AUTOMATION, TASK_UPDATE, CALENDAR_SYNC, CHAT_MENTION
)

@Entity(tableName = "automation_rules")
data class AutomationRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val department: String,
    val triggerEvent: String,
    val actionResult: String,
    val isEnabled: Boolean = true,
    val runCount: Int = 0
)

data class TeamMemberPresence(
    val id: String,
    val name: String,
    val role: String,
    val department: String,
    val status: String, // "Coding", "In Standup", "In Meeting", "Deep Work", "Available"
    val isOnline: Boolean,
    val currentTaskTitle: String = ""
)
