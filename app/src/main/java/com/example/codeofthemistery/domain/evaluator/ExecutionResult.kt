package com.example.codeofthemistery.domain.evaluator

/** Result returned by [MiniPythonEvaluator] after parsing, executing, and validating user code. */
data class ExecutionResult(
    val isSuccess: Boolean,
    val isRulePassed: Boolean,
    val exitCode: Int,
    val stdoutLines: List<String>,
    val variables: Map<String, Any?>,
    val errorMessage: String? = null
)
