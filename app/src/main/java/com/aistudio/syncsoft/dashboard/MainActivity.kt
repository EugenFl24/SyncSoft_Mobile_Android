package com.aistudio.syncsoft.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aistudio.syncsoft.dashboard.data.TaskEntity
import com.aistudio.syncsoft.dashboard.ui.SyncsoftViewModel
import com.aistudio.syncsoft.dashboard.ui.screens.AddAutomationRuleDialog
import com.aistudio.syncsoft.dashboard.ui.screens.AddCalendarEventDialog
import com.aistudio.syncsoft.dashboard.ui.screens.AddTaskDialog
import com.aistudio.syncsoft.dashboard.ui.screens.AiTaskBreakdownDialog
import com.aistudio.syncsoft.dashboard.ui.screens.AttachFileModal
import com.aistudio.syncsoft.dashboard.ui.screens.AutomationsScreen
import com.aistudio.syncsoft.dashboard.ui.screens.CalendarScreen
import com.aistudio.syncsoft.dashboard.ui.screens.ChatScreen
import com.aistudio.syncsoft.dashboard.ui.screens.DashboardScreen
import com.aistudio.syncsoft.dashboard.ui.screens.ExecutiveReportModal
import com.aistudio.syncsoft.dashboard.ui.screens.LiveHuddleModal
import com.aistudio.syncsoft.dashboard.ui.screens.ProjectDetailModal
import com.aistudio.syncsoft.dashboard.ui.screens.ProjectsTasksScreen
import com.aistudio.syncsoft.dashboard.ui.screens.SyncsoftTopHeader
import com.aistudio.syncsoft.dashboard.ui.screens.TaskDetailModal
import com.aistudio.syncsoft.dashboard.ui.screens.TeamMemberModal
import com.aistudio.syncsoft.dashboard.ui.theme.SyncsoftTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SyncsoftViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            SyncsoftTheme(themeMode = themeMode) {
                SyncsoftAppContent(viewModel = viewModel, currentThemeMode = themeMode)
            }
        }
    }
}

@Composable
fun SyncsoftAppContent(
    viewModel: SyncsoftViewModel,
    currentThemeMode: com.aistudio.syncsoft.dashboard.ui.theme.ThemeMode
) {
    val selectedDept by viewModel.selectedDepartment.collectAsStateWithLifecycle()
    val mainTab by viewModel.selectedMainTab.collectAsStateWithLifecycle()

    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val calendarEvents by viewModel.calendarEvents.collectAsStateWithLifecycle()
    val activityLogs by viewModel.activityLogs.collectAsStateWithLifecycle()
    val automationRules by viewModel.automationRules.collectAsStateWithLifecycle()
    val teamPresence by viewModel.teamPresence.collectAsStateWithLifecycle()

    val selectedChannel by viewModel.selectedChannel.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    val showAddTask by viewModel.showAddTaskDialog.collectAsStateWithLifecycle()
    val showAddCalendar by viewModel.showAddCalendarDialog.collectAsStateWithLifecycle()
    val showAiBreakdown by viewModel.showAiBreakdownDialog.collectAsStateWithLifecycle()
    val showAddAutomationRule by viewModel.showAddAutomationRuleDialog.collectAsStateWithLifecycle()
    val showAttachFile by viewModel.showAttachFileDialog.collectAsStateWithLifecycle()

    val selectedTaskDetail by viewModel.selectedTaskDetail.collectAsStateWithLifecycle()
    val selectedProjectDetail by viewModel.selectedProjectDetail.collectAsStateWithLifecycle()
    val selectedMemberDetail by viewModel.selectedMemberDetail.collectAsStateWithLifecycle()
    val activeHuddleTitle by viewModel.activeHuddleTitle.collectAsStateWithLifecycle()

    val isGeneratingReport by viewModel.isGeneratingReport.collectAsStateWithLifecycle()
    val standupReportResult by viewModel.standupReportResult.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SyncsoftTopHeader(
                selectedDepartment = selectedDept,
                currentThemeMode = currentThemeMode,
                onDepartmentSelected = { viewModel.setDepartment(it) },
                onGenerateAiReport = { viewModel.generateExecutiveStandupReport() },
                onCycleTheme = { viewModel.cycleThemeMode() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                val navItems = listOf(
                    Triple("Dashboard", Icons.Default.Dashboard, 0),
                    Triple("Tasks", Icons.Default.TaskAlt, 1),
                    Triple("Calendar", Icons.Default.CalendarMonth, 2),
                    Triple("Chat", Icons.Default.Chat, 3),
                    Triple("Automations", Icons.Default.ElectricBolt, 4)
                )

                navItems.forEach { (title, icon, index) ->
                    val isSelected = mainTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setMainTab(index) },
                        icon = { Icon(imageVector = icon, contentDescription = title) },
                        label = {
                            Text(
                                text = title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_$index")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (mainTab) {
                0 -> DashboardScreen(
                    projects = projects,
                    tasks = tasks,
                    activityLogs = activityLogs,
                    teamPresence = teamPresence,
                    onGenerateReportClick = { viewModel.generateExecutiveStandupReport() },
                    onNavigateToTasks = { viewModel.setMainTab(1) },
                    onNavigateToCalendar = { viewModel.setMainTab(2) },
                    onNavigateToChat = { viewModel.setMainTab(3) },
                    onProjectClick = { viewModel.selectedProjectDetail.value = it },
                    onMemberClick = { viewModel.selectedMemberDetail.value = it }
                )

                1 -> ProjectsTasksScreen(
                    tasks = tasks,
                    onStatusChange = { taskId, newStatus, taskTitle, dept ->
                        viewModel.updateTaskStatus(taskId, newStatus, taskTitle, dept)
                    },
                    onAddTaskClick = { viewModel.showAddTaskDialog.value = true },
                    onAiBreakdownClick = { viewModel.showAiBreakdownDialog.value = true },
                    onSelectTaskDetail = { viewModel.selectedTaskDetail.value = it }
                )

                2 -> CalendarScreen(
                    events = calendarEvents,
                    onAddEventClick = { viewModel.showAddCalendarDialog.value = true },
                    onJoinHuddle = { viewModel.openHuddle(it) }
                )

                3 -> ChatScreen(
                    selectedChannel = selectedChannel,
                    messages = chatMessages,
                    onChannelSelect = { viewModel.setChannel(it) },
                    onSendMessage = { viewModel.sendChatMessage(it) },
                    onStartHuddle = { viewModel.openHuddle(it) },
                    onAttachFile = { viewModel.showAttachFileDialog.value = true }
                )

                4 -> AutomationsScreen(
                    rules = automationRules,
                    onToggleRule = { ruleId, enabled -> viewModel.toggleAutomationRule(ruleId, enabled) },
                    onAiAuditClick = { viewModel.generateExecutiveStandupReport() },
                    onAddRuleClick = { viewModel.showAddAutomationRuleDialog.value = true }
                )
            }

            // Dialog Modals
            if (showAddTask) {
                AddTaskDialog(
                    onDismiss = { viewModel.showAddTaskDialog.value = false },
                    onConfirm = { title, description, department, assigneeName, priority, dueDate, isAutomated ->
                        viewModel.addTask(
                            projectId = "proj-1",
                            projectTitle = "Syncsoft Core Dashboard v3.0",
                            title = title,
                            description = description,
                            department = department,
                            assigneeName = assigneeName,
                            priority = priority,
                            dueDate = dueDate,
                            isAutomated = isAutomated
                        )
                    }
                )
            }

            if (showAddCalendar) {
                AddCalendarEventDialog(
                    onDismiss = { viewModel.showAddCalendarDialog.value = false },
                    onConfirm = { title, description, department, startTime, endTime, dateString, eventType, attendeesCsv, syncPlatform ->
                        viewModel.addCalendarEvent(
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
                )
            }

            if (showAiBreakdown) {
                AiTaskBreakdownDialog(
                    isLoading = isGeneratingReport,
                    onDismiss = { viewModel.showAiBreakdownDialog.value = false },
                    onConfirm = { projectTitle, prompt, department ->
                        viewModel.triggerAiAutoBreakdown(projectTitle, prompt, department)
                    }
                )
            }

            if (showAddAutomationRule) {
                AddAutomationRuleDialog(
                    onDismiss = { viewModel.showAddAutomationRuleDialog.value = false },
                    onConfirm = { title, dept, trigger, action ->
                        viewModel.addAutomationRule(title, dept, trigger, action)
                    }
                )
            }

            if (showAttachFile) {
                AttachFileModal(
                    onDismiss = { viewModel.showAttachFileDialog.value = false },
                    onConfirm = { fileName, fileType ->
                        viewModel.attachFileAndSend(fileName, fileType)
                    }
                )
            }

            selectedTaskDetail?.let { task ->
                TaskDetailModal(
                    task = task,
                    onDismiss = { viewModel.selectedTaskDetail.value = null },
                    onStatusChange = { newStatus ->
                        viewModel.updateTaskStatus(task.id, newStatus, task.title, task.department)
                        viewModel.selectedTaskDetail.value = null
                    }
                )
            }

            selectedProjectDetail?.let { project ->
                ProjectDetailModal(
                    project = project,
                    onDismiss = { viewModel.selectedProjectDetail.value = null },
                    onUpdateStage = { newStage ->
                        viewModel.updateProjectStage(project.id, newStage)
                    }
                )
            }

            selectedMemberDetail?.let { member ->
                TeamMemberModal(
                    member = member,
                    onDismiss = { viewModel.selectedMemberDetail.value = null },
                    onStartHuddle = {
                        viewModel.openHuddle("1:1 Huddle with ${member.name}")
                        viewModel.selectedMemberDetail.value = null
                    },
                    onStartChat = {
                        viewModel.setMainTab(3)
                        viewModel.sendChatMessage("@${member.name} Hey! Do you have a minute to review the latest Syncsoft sprint updates?")
                        viewModel.selectedMemberDetail.value = null
                    }
                )
            }

            activeHuddleTitle?.let { huddleTitle ->
                LiveHuddleModal(
                    huddleTitle = huddleTitle,
                    onDismiss = { viewModel.closeHuddle() }
                )
            }

            standupReportResult?.let { reportText ->
                ExecutiveReportModal(
                    reportText = reportText,
                    onDismiss = { viewModel.standupReportResult.value = null }
                )
            }
        }
    }
}
