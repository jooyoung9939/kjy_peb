package com.example.madcamp_week2_kjy_peb


import java.io.Serializable
import java.util.HexFormat
import java.util.function.BinaryOperator

data class RegisterModel(
    var id: String,
    var pw: String,
    var mbti: Int,
    var hobby: Int,
    var region: Int,
    var image: String
)


data class RegisterResult(
    var message: Boolean
)

data class LoginModel(
    var id: String,
    var pw: String
)

data class LoginResult(
    var UID: Int,
    var accessToken: String
)

data class User(
    val UID: Int,
    val users_id: String,
    val users_pw: String,
    val users_mbti: String,
    val users_hobby: String,
    val users_region: String
): Serializable
