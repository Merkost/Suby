package com.merkost.suby

import androidx.compose.foundation.shape.RoundedCornerShape
import com.merkost.suby.model.Category
import com.merkost.suby.model.Service

val servicesByCategory: Map<Category, List<Service>> =
    Category.values()
        .associateWith { category ->
            if (category == Category.CUSTOM) {
                emptyList()
            } else {
                Service.values().filter { it.category == category }
            }
        }

val SubyShape = RoundedCornerShape(25)



