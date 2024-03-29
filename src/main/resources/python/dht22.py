import Adafruit_DHT
import json
import sys

# Set sensor type: DHT22
sensor = Adafruit_DHT.DHT22

# Set GPIO pin number (BCM numbering)
pin = 4

# Read data from sensor
humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)

# Print results
if humidity is not None and temperature is not None:
    data = {'temperature': temperature, 'temperatureF': ((temperature * (9/5)) + 32),  'humidity': humidity}
    print(json.dumps(data))
else:
    print(json.dumps("Error pulling from DHT22."), file=sys.stderr)