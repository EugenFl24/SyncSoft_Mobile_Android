package com.aistudio.syncsoft.dashboard.network

import com.aistudio.syncsoft.dashboard.data.ProjectEntity
import retrofit2.http.GET

interface SyncSoftApiService {
    
    // We will define the endpoints based on Next.js /api routes
    
    @GET("api/projects")
    suspend fun getProjects(): List<ProjectEntity>

    // Add more endpoints as we map them from Next.js (e.g., users, tasks)
}
