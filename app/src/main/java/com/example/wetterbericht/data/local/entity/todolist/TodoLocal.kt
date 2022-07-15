package com.example.wetterbericht.data.local.entity.todolist

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "TodoTable")
data class TodoLocal(
    @PrimaryKey
    val taskID : String,
    val title: String,
    val description: String,
    val levelColor : Int,
    val dateStart: String,
    val dateDay : Int,
    val dateDueMillis : Long,
    val notificationInterval : Int,
    val startTime : String,
    val endTime : String,

    @ColumnInfo(name = "completed")
    val isComplete: Boolean,
):Parcelable

@Entity
data class TodoSubTask(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val title : String,
    val isComplete: Boolean,
    val todoId : String
)
//one to many
data class TodoandSubTask(
    @Embedded
    val todo : TodoLocal,
    @Relation(
        parentColumn = "taskID",
        entityColumn = "todoId"
    )
    val subtask : List<TodoSubTask>
)