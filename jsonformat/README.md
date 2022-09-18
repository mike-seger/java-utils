# json-format

This JSON formatter uses jackson.databind.ObjectMapper

## build + run
```
./gradlew clean build
json='{"one":"AAA","two":["B\"BB","CCC"],"three":{"four":"DDD","five":["EEE","FFF",{"ABC",123},"GGG","HHH"],"six":[1,2,3,4,5,6,7,8,9,10],"seven": 999}}'
echo "$json" | java -jar build/libs/*.jar
```

## build + run (alternative)
```
./gradlew clean build swayamJar
json=...
echo "$json" | build/json-format-0.0.1-SNAPSHOT.sh
```

## package application (jdk 14+)
```
jpackage --input build/libs --main-jar json-format-0.0.1-SNAPSHOT.jar
```

## build native executable
```
native-image -cp build/classes/java/main com.net128.app.jsonformat.JsonFormatter
```

## Other formatters
- https://stedolan.github.io/jq/  
  https://jqplay.org/
  ```
  jq -M . | jq -MRsr 'gsub("\n      +";"")|gsub("\n    ]";"]")'
  ```
- https://j-brooke.github.io/FracturedJson/
