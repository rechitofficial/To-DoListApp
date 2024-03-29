package com.dicoding.todoapp.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

//TODO 2 : Define data access object (DAO)
@Dao
interface TaskDao {
    @RawQuery(observedEntities = [Task::class])
    fun getTasks(query: SupportSQLiteQuery): DataSource.Factory<Int, Task>
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): LiveData<Task>
    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY dueDate ASC")
    fun getNearestActiveTask(): Task
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg tasks: Task)
    @Delete
    suspend fun deleteTask(task: Task)
    @Query("UPDATE tasks Set completed = :completed where id LIKE :taskId")
    suspend fun updateCompleted(taskId: Int, completed: Boolean)
    
}
