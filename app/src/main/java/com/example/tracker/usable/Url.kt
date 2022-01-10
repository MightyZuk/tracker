package com.example.tracker.usable

class Url {
    companion object{

            val list : MutableList<String> = mutableListOf()

//            const val getClientData = "http://192.168.1.5/Employee/getClientData.php"//home
        const val getClientData = "http://192.168.1.49/Employee/getClientData.php" //intern

//            const val getData = "http://192.168.1.5/Employee/getData.php" //home
        const val getData = "http://192.168.1.49/Employee/getData.php" //intern

//            const val putData = "http://192.168.1.5/Employee/putData.php" //home
        const val putData = "http://192.168.1.49/Employee/putData.php" //intern

//            const val putClientData = "http://192.168.1.5/Employee/putClientData.php" //home
        const val putClientData = "http://192.168.1.49/Employee/putClientData.php" //intern

//            const val login = "http://192.168.1.5/Employee/login.php" //home
        const val login = "http://192.168.1.49/Employee/login.php" //intern

//            const val checkNumber = "http://192.168.1.5/Employee/checkNumberExists.php" //home
        const val checkNumber = "http://192.168.1.49/Employee/checkNumberExists.php" //intern

        const val putLocation = "http://192.168.1.49/Employee/putLocation.php"
        }
}