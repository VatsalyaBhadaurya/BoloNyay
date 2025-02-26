package com.example.legalapp.models

data class Form(
    val id: String,
    val title: String,
    val type: String,
    val applicantName: String,
    val description: String,
    val relief: String,
    val submissionDate: String,
    val status: String
) 