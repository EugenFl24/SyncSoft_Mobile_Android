package com.aistudio.syncsoft.dashboard.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.syncsoft.dashboard.data.ActivityLogEntity
import com.aistudio.syncsoft.dashboard.data.AutomationRuleEntity
import com.aistudio.syncsoft.dashboard.data.CalendarEventEntity
import com.aistudio.syncsoft.dashboard.data.ChatMessageEntity
import com.aistudio.syncsoft.dashboard.data.ProjectEntity
import com.aistudio.syncsoft.dashboard.data.SyncsoftDatabase
import com.aistudio.syncsoft.dashboard.data.SyncsoftRepository
import com.aistudio.syncsoft.dashboard.data.TaskEntity
import com.aistudio.syncsoft.dashboard.data.TeamMemberPresence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SyncsoftViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SyncsoftRepository

    init {
        val dao = SyncsoftDatabase.getDatabase(application).syncsoftDao()
        repository = SyncsoftRepository(dao)
        viewModelScope.launch {
            repository.refreshProjects()
        }
    }

    // Navigation & Filters
    private val _themeMode = MutableStateFlow(com.aistudio.syncsoft.dashboard.ui.theme.ThemeMode.LIGHT)
    val themeMode: StateFlow<com.aistudio.syncsoft.dashboard.ui.theme.ThemeMode> = _themeMode.asStateFlow()

    private val _selectedDepartment = MutableStateFlow("All Departments")
    val selectedDepartment: StateFlow<String> = _selectedDepartment.asStateFlow()

    private val _selectedMainTab = MutableStateFlow(0) // 0: Dashboard, 1: Projects & Tasks, 2: Calendar, 3: Chat, 4: Automations
    val selectedMainTab: StateFlow<Int> = _selectedMainTab.asStateFlow()

    private val _selectedChannel = MutableStateFlow("general")
    val selectedChannel: StateFlow<String> = _selectedChannel.asStateFlow()

    // Dialog & Detail Controls
    val showAddTaskDialog = MutableStateFlow(false)
    val showAddCalendarDialog = MutableStateFlow(false)
    val showAiBreakdownDialog = MutableStateFlow(false)
    val showAddAutomationRuleDialog = MutableStateFlow(false)
    val showAttachFileDialog = MutableStateFlow(false)

    val selectedTaskDetail = MutableStateFlow<TaskEntity?>(null)
    val selectedProjectDetail = MutableStateFlow<ProjectEntity?>(null)
    val selectedMemberDetail = MutableStateFlow<TeamMemberPresence?>(null)
    val activeHuddleTitle = MutableStateFlow<String?>(null)

    // AI Standup Generation State
    val isGeneratingReport = MutableStateFlow(false)
    val standupReportResult = MutableStateFlow<String?>(null)

    // Data Streams
    val projects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<TaskEntity>> = _selectedDepartment
        .flatMapLatest { dept -> repository.getTasksByDepartment(dept) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendarEvents: StateFlow<List<CalendarEventEntity>> = repository.allCalendarEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<ActivityLogEntity>> = repository.activityLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automationRules: StateFlow<List<AutomationRuleEntity>> = repository.automationRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teamPresence: StateFlow<List<TeamMemberPresence>> = repository.teamPresence

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _selectedChannel
        .flatMapLatest { channel -> repository.getChatMessages(channel) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setThemeMode(mode: com.aistudio.syncsoft.dashboard.ui.theme.ThemeMode) {
        _themeMode.value = mode
    }

    fun cycleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            com.aistudio.syncsoft.dashboard.ui.theme.ThemeMode.LIGHT -> com.aistudio.syncsoft.dashboard.ui.theme.ThemeMode.DARK
            com.aistudio.syncsoft.dashboard.ui.theme.ThemeMode.DARK -> com.aistudio.syncsoft.dashboard.ui.theme.ThemeMode.HIGH_SUNLIGHT
            com.aistudio.syncsoft.dashboard.ui.theme.ThemeMode.HIGH_SUNLIGHT -> com.aistudio.syncsoft.dashboard.ui.theme.ThemeMode.LIGHT
        }
    }

    fun setDepartment(dept: String) {
        _selectedDepartment.value = dept
    }

    fun setMainTab(tabIndex: Int) {
        _selectedMainTab.value = tabIndex
    }

    fun setChannel(channelId: String) {
        _selectedChannel.value = channelId
    }

    fun addTask(
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
    ) {
        viewModelScope.launch {
            repository.addTask(
                projectId = projectId,
                projectTitle = projectTitle,
                title = title,
                description = description,
                department = department,
                assigneeName = assigneeName,
                priority = priority,
                dueDate = dueDate,
                isAutomated = isAutomated,
                automationTrigger = automationTrigger
            )
        }
    }

    fun updateTaskStatus(taskId: Long, newStatus: String, taskTitle: String, department: String) {
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, newStatus, taskTitle, department)
        }
    }

    fun addCalendarEvent(
        title: String,
        description: String,
        department: String,
        startTime: String,
        endTime: String,
        dateString: String,
        eventType: String,
        attendeesCsv: String,
        syncPlatform: String
    ) {
        viewModelScope.launch {
            repository.addCalendarEvent(
                title = title,
                description = description,
                department = department,
                startTime = startTime,
                endTime = endTime,
                dateString = dateString,
                eventType = eventType,
                attendeesCsv = attendeesCsv,
                syncPlatform = syncPlatform
            )
        }
    }

    fun sendChatMessage(messageText: String) {
        if (messageText.isBlank()) return
        viewModelScope.launch {
            repository.sendChatMessage(
                channelId = _selectedChannel.value,
                senderName = "Alex Rivera",
                messageText = messageText
            )
        }
    }

    fun toggleAutomationRule(ruleId: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAutomationRule(ruleId, enabled)
        }
    }

    fun triggerAiAutoBreakdown(projectTitle: String, prompt: String, department: String) {
        viewModelScope.launch {
            isGeneratingReport.value = true
            repository.runAIAutoBreakdown(projectTitle, prompt, department)
            isGeneratingReport.value = false
            showAiBreakdownDialog.value = false
        }
    }

    fun generateExecutiveStandupReport() {
        viewModelScope.launch {
            isGeneratingReport.value = true
            val report = repository.generateStandupReport(tasks.value, selectedDepartment.value)
            standupReportResult.value = report
            isGeneratingReport.value = false
        }
    }

    fun addAutomationRule(title: String, department: String, triggerEvent: String, actionResult: String) {
        viewModelScope.launch {
            repository.addAutomationRule(title, department, triggerEvent, actionResult)
            showAddAutomationRuleDialog.value = false
        }
    }

    fun updateProjectStage(projectId: String, newStage: String) {
        viewModelScope.launch {
            repository.updateProjectStage(projectId, newStage)
            // Update selected project state if opened
            selectedProjectDetail.value = selectedProjectDetail.value?.copy(stage = newStage)
        }
    }

    fun openHuddle(title: String) {
        activeHuddleTitle.value = title
    }

    fun closeHuddle() {
        activeHuddleTitle.value = null
    }

    fun attachFileAndSend(fileName: String, fileType: String, codeSnippet: String = "") {
        val message = "📎 Attached [$fileType] $fileName\n```\n$codeSnippet\n```"
        sendChatMessage(message)
        showAttachFileDialog.value = false
    }
}
