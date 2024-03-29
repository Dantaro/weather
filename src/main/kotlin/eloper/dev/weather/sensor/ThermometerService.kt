package eloper.dev.weather.sensor

import eloper.dev.weather.python.PythonRunner

class ThermometerService(
    private val pythonLocation: String
): SensorService<ThermometerReadout> {
    override suspend fun getReadout(): ThermometerReadout? {
        return PythonRunner.runPythonScript(
            PythonRunner.PythonArgs(
                scriptLoc = "$pythonLocation/dht22.py",
                timeout = 50000L,
                params = null
            )
        )
    }
}

data class ThermometerReadout(
    val temperature: Float,
    val temperatureF: Float,
    val humidity: Float
)