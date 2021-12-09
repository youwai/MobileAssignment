package com.example.mobileassignment

data class Materials(var serialNo: String? = "", var partNo: String?  = "", var quantity: Int? = 0, var status: Int? = 0, var rackInBy: String? = "",
                     var rackInDate: String? = "", var rackOutDate: String? = " - ", var rackOutBy: String? = " - ")
