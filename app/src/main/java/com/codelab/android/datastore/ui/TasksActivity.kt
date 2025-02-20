/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelab.android.datastore.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import com.codelab.android.datastore.UserPreferences
import com.codelab.android.datastore.data.TasksRepository
import com.codelab.android.datastore.data.UserPreferencesRepository
import com.codelab.android.datastore.databinding.ActivityTasksBinding

class TasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTasksBinding
    private val adapter = TasksAdapter()
    private var wasStarted = false
    private lateinit var viewModel: TasksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(
            this,
            TasksViewModelFactory(TasksRepository, UserPreferencesRepository.getInstance(this))
        ).get(TasksViewModel::class.java)

        setupRecyclerView()
        setupFilterListeners(viewModel)
        setupSort()

        /*val actualCounter = viewModel.getAppCounter()
        binding.appStartCounter.text = actualCounter.toString()
        viewModel.updateStartCounter(actualCounter)*/

        viewModel.tasksUiModel.observe(owner = this) { tasksUiModel ->
            adapter.submitList(tasksUiModel.tasks)
            updateSort(tasksUiModel.sortOrder)
            binding.showCompletedSwitch.isChecked = tasksUiModel.showCompleted
        }

        //viewModel.updateStartCounter(tasksUiModel.startAppCounter + 1)
        //Log.i("test", viewModel.tasksUiModel.value?.startAppCounter.toString());

        //viewModel.updateStartCounter(viewModel.tasksUiModel.value?.startAppCounter);

        //viewModel.tasksUiModel.run {  }
        viewModel.tasksUiModel.observe(owner = this) { tasksUiModel ->
            if(!wasStarted)
            {
                viewModel.updateStartCounter(tasksUiModel.startAppCounter)
                wasStarted = true
            }

            adapter.submitList(tasksUiModel.tasks)
            binding.appStartCounter.text = tasksUiModel.startAppCounter.toString()
        }
    }


    private fun setupFilterListeners(viewModel: TasksViewModel) {
        binding.showCompletedSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.showCompletedTasks(checked)
        }
    }

    private fun setupRecyclerView() {
        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)

        binding.list.adapter = adapter
    }

    private fun setupSort() {
        binding.sortDeadline.setOnCheckedChangeListener { _, checked ->
            viewModel.enableSortByDeadline(checked)
        }
        binding.sortPriority.setOnCheckedChangeListener { _, checked ->
            viewModel.enableSortByPriority(checked)
        }
    }


    private fun updateSort(sortOrder: UserPreferences.SortOrder) {
        binding.sortDeadline.isChecked =
            sortOrder == UserPreferences.SortOrder.BY_DEADLINE || sortOrder == UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY
        binding.sortPriority.isChecked =
            sortOrder == UserPreferences.SortOrder.BY_PRIORITY || sortOrder == UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY
    }
}
