package com.tracker.personalbudgetplanner.ui.category.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.tracker.personalbudgetplanner.utils.constants.DbConstants

@Entity(
    tableName = DbConstants.table_categories, foreignKeys = [
        ForeignKey(
            entity = IconEntity::class,
            parentColumns = ["id"],
            childColumns = ["iconId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val iconId: Int,
    val type: String
)

data class CategoryWithIcon(
    @Embedded val category: CategoryEntity,
    @Relation(parentColumn = "iconId", entityColumn = "id")
    val icon: IconEntity
)