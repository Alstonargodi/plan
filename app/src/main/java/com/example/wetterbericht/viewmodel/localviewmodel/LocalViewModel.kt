package com.example.wetterbericht.viewmodel.localviewmodel

import androidx.lifecycle.*
import com.example.wetterbericht.domain.LocalUseCase
import com.example.wetterbericht.model.local.*
import com.example.wetterbericht.model.local.entity.habits.HabitsLocal
import com.example.wetterbericht.model.local.entity.todolist.TodoLocal
import com.example.wetterbericht.model.local.entity.todolist.TodoSubTask
import com.example.wetterbericht.model.local.entity.todolist.TodoandSubTask
import com.example.wetterbericht.model.local.entity.weather.WeatherLocal
import com.example.wetterbericht.model.repository.localrepository.LocalRepository

class LocalViewModel(
    private val repository: LocalRepository,
    private val todoUseCase: LocalUseCase
): ViewModel() {

    fun getOnboardingStatus(): LiveData<Boolean> = repository.getOnboardingStatus()

    suspend fun savePreferences(onBoard : Boolean){
        repository.savePreferences(onBoard)
    }

    fun readTodoLocalUse(): LiveData<List<TodoLocal>> =
        todoUseCase.readTodoLocal()


    fun readWeatherLocal(): LiveData<List<WeatherLocal>> =
        repository.readWeather()

    fun readAlarmChip(): LiveData<List<ChipAlarm>> =
        todoUseCase.readChipTime()


    fun readTodo(name: String): LiveData<List<TodoLocal>> =
        repository.readSearchTodo(name)


    fun readTodoSubtask(name : String): LiveData<List<TodoandSubTask>> =
        repository.readTodoSubtask(name)


    fun getTodayTask(): LiveData<List<TodoLocal>> =
        todoUseCase.getTodayTask()

    fun getUpcomingTask(): LiveData<List<TodoLocal>> =
        repository.getUpcomingTask()

    fun getPreviousTask(): LiveData<List<TodoLocal>> =
        repository.getPreviousTask()

    fun insertTodoLocal(data : TodoLocal) = todoUseCase.insertTodoList(data)


    fun insertSubtask(data : TodoSubTask) = todoUseCase.insertSubtask(data)


    fun insertWeatherLocal(data : WeatherLocal) = repository.insertWeather(data)


    fun insertAlarmChip(alarm : ChipAlarm) = todoUseCase.insertChipTime(alarm)


    fun searchLocation(name: String): LiveData<List<WeatherLocal>> =
        repository.searchLocation(name)


    fun deleteTodoLocal(name : String) = repository.deleteTodo(name)

    fun readHabits(): LiveData<List<HabitsLocal>> =
        repository.readHabits()

    fun insertHabits(data : HabitsLocal) =
        repository.insertHabits(data)

    fun deleteHabits(name : String)=
        repository.deleteHabits(name)

}