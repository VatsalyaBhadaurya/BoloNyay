package com.example.legalapp.models

data class LegalCase(
    val caseNumber: String,
    val title: String,
    val court: String,
    val category: String,
    val date: String,
    val status: String,
    val description: String
) 