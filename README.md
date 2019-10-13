[![License: GPL v3+](https://img.shields.io/badge/License-GPL%20v3%2B-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) ![Tec Version: V3.9](https://img.shields.io/badge/Tec%20Version-V3.9--ALPHA-red) ![Tecc Version: V1.0](https://img.shields.io/badge/Tecc%20Version-V1.0-red)

# tec
'**tec**' is a new programming languge that is user friendly as you can just write your Code. You do not have to mess with any **main** functions if you do not want to. If you want to start with tec you do not need to have any knowledge of any programming languages what so ever.
To change to a new language you can use this as a ground basis to change to Java, Swift, Python, JavaScript, Go. If you have any knowledge in Java, Swift, Python, Go or JavaScript you should not have any trouble with writing programs in **tec** as soon as you know the basics.

## Getting started
Create a file with the suffix **.tec**. Open the file and now you can write your first code. Start with a 'Hello World' programm.
```
print "Hello World"
```
If you execute this Program it will output Hello World.

## Comments
One line Comments start with '//'. There are also Block comments which start with '\*' at the start of a line and end with '*/' at the end of a line.
```
// This prints "Hello World"
print "Hello World"

/*Here are some prints for some formating
also this system writes "Hello World" 3 Times*/
print ""
print "Hello World"
print "Hello World"
print "Hello World"
print ""
```

## Variables
#### Creating
There are 2 types of variables, constant ones and dynamic ones. Dynamic variables are changeable and can be reassigned.
The variable type will get evaluated and automatically set.
```
// This Variable is an Integer Variable '0'
var int = 0
var int2 = ##1

// This Variable is a Float Variable '0.0'
var num = 0.0

// This Variable is a Character Variable 'c'
var chr = 'c'

// This Variable is a Boolean Variable 'true'
var bol = true

// This Variable is a String Variable 'Hello World'
var str = "Hello World"
var str2 = 'Hello World'

// To Create a Constant instead of using 'var' use 'let'
let const = "HELP"
```

#### Reassigning
You can reassign a dynamic variable by writing its name and the Assign operator '=' behind it. After the Operator there is the new Value the variable should hold.
If you only want to increment a Integer or Number variable by 1 or decrement it by 1 use '++' and '--'. You can also use '+=' followed by the increment value. Instead of the '+=' to increment you can also use '-=' to decrement the variable by a specific value.
If you want to multiply a variable by a specific value use '*=' and reverse it with '/='. You can also calculate the Power of a variable with '^='. To direct access the modulo function use '%='.
```
var i = 0
print i
i++
print i
i--
print i
i += 10
print i
i -= 5
print i
i *= 6
print i
i /= 3
print i
i ^= 2
print i
i %= 2
print i
```
The output of the Code above is as follows.
```
0
1
0
10
5
30
10
100
0
```

## Datatypes
There are 5 datatypes. The datatypes are chr for Character, bol for Boolean, num for Float, int for Integer and str for String.
```
*chr '<One Character>'
*bol <true/false>
*num <Floating point number>
*int <Non Floating point Number or Hex Number with ##>
*str "One or more Characters" or /*'Many Characters'*/
```

## Advanced Printing
To print a Value of a variable you can write the variable name behind the print statement. The print statement can also calculate stuff and you can add calculations and variables together.
```
// This print will calculate the sum of the value of 'i' and 1
var i = 1
print i + 1

// This print will output "c2"
print "c" + (i+1)

// You can also calculate the sum of 2 Variables
var j = 3
print j + i

// To put strings together you can also use +
let k = " World"
print "Hello" + k 
```

## Slowing things down
Use the 'sleep' statement with a milliseconds time behind it to sleep for x milliseconds.
```
print "Starting to Sleep"

sleep 1000

print "Finished to Sleep"
```

## Conditional Statements

#### Basic 'If' statements
If you want to choose what you do, use 'if'.
```
// Use an Boolean Expression in the Brackets of the 'if' statement.
if (true) {

}

if (1 == 1) {

}

if (true == false) {

}

if (true || false) {

}
```

#### Intermediary 'If' statements 
You can also use variables in Boolean expressions. The easiest is a boolean variable because you do not need to compare it to anything else.
To compare use '==', '!=', '>', '<', '>=', '<='. Also you have some basic and advanced logic operators as || (or), && (and), and !! (not). Just have in mind that you need to use '<<', '>>' or '«', '»' as priority definition in Boolean expressions.
Advanced logic operators are !& (nand), x| (xor), n| (nor) and xn (xnor).
Have in mind that the not operator '!!' needs to be before the Boolean value.
You can also have a Boolean expression after a print statement.
```
// All Logic Operators
var b1 = true
var b2 = false

print b1 || b2
print b1 && b2
print !! b1
print b1 !& b2
print b1 x| b2
print b1 n| b2
print b1 xn b2

// Variables in Boolean Expressions
var i1 = 0

if (i1 == 0) {
    print "i1 is zero"
}
```

#### Advanced 'If' statements
When the 'if' failed you can use the 'else' statement. When you want to check if something is something, do one thing and if something is something else, do another thing, use the 'else if', and if nothing matches do a third things, use the 'else'.
```
var i = 0

if (i == 0) {
    print "i is zero"
} else if (i == 1) {
    print "i is one"
} else {
    print "i is not zero or one"
}
```
You can also check if 2 strings are equal, for that use 'equals'. If you want to have it not case sensitive, use 'equalsIgnoreCase'. When you want to check if the first string starts with the other string, use 'startsWith'. Use 'endsWith' provided you want to check if the first string ends with the second string. To check if the first string contains the second, use 'contains'. This can also be used non case sensitive, with 'containsIgnoreCase'.
If you want to check if a variable is of type x you can use the 'typeof' keyword. To check if something can be some other type use the 'canbe' keyword. Both keywords needs a type following!


## Repeating
To repeat something multiple times use the 'while' loop. This loop will check first before executing the 'Loop Part'.
```
// Loop through the Numbers from zero to ten

var i = 0
while (i < 10) {
    print "Loop Iteration: " + i
    i = i + 1
}
```

## Functions

#### Definition
Functions are used to declare one code snippet that you want to use multiple times throughout your project. The 'func' key will get used. After that there is the name of your function followed by an opening and closing bracket.
```
// This is a valid function
func example() {

}
```

#### Calling
To call a function use the function name with opening and closing bracket after it.
```
// This is to call the function.
example()

// This is a valid function
func example() {

}
```
You can call the function either above the Definition or below it.
```
// This is a valid function
func example() {

}

// This is to call the function.
example()
```

#### Parameter
A function can have Parameters. These parameters are having a type and a name. To add a Parameter to a function the parameter type followed by the name needs to be put into the '()' brackets. To have multiple Parameters you need to separate each one by a colon ','.
```
// Function with one Parameter
func oneParameter(*int i) {

}

// Function with two Parameters
func twoParameters(*int i, *str s) {

}
```
To call a function with Parameters as Input write your input in the call brackets. The parameter needs to have the same type as the parameter of the function you want to call.
```
// Function with one Parameter
func oneParameter(*int i) {

}

// Function with two Parameter
func twoParameters(*int i, *str s) {

}

// Call the Function 'oneParameter' with the parameter 1
oneParameter(1)

// Call the Function 'twoParameters' with the parameters 1, "Hello"
twoParameters(1, "Hello")
```

#### Function Overloading
You can also define the same function multiple times with different Parameters. It is not allowed to have the same function twice. So with the same parameter types in the same order.
```
// This defines the function 'overloaded' with a Parameter i of type int
func overloaded(*int i) {

}

// This defines the function 'overloaded' with a Parameter i of type string
func overloaded(*str i) {

}
```
The Executor automatically calls the correct function to your parameters. At least if you have a function with that kind of parameter configuration.
```
// This defines the function 'overloaded' with a Parameter i of type int
func overloaded(*int i) {

}

// This defines the function 'overloaded' with a Parameter i of type string
func overloaded(*str i) {

}

// This will call the 'overloaded(*int i)' function
overloaded(1)

// This will call the 'overloaded(*str i)' function
overloaded("1")
```

#### Parameter Expressions
You can also use a Parameter with variables as well as an expression as parameter.
```
// The Function Declaration
func parameterExpressions(*int i) {

}

// Function call with Expression Syntax
parameterExpressions(1+1)

var i = 0
parameterExpressions(i+1)
```

#### Return Statement
The return statement can be used to exit a function before the real block exit of the function. But it can also return some value. To return something with a specific value write an expression behind it and see what happens.
If you return from the Main thread it gets discontinued.
```
// This will give the Output '1'
testReturn(1)

// This wont give any Output
testReturn(-1)

return

// This Statement wont be executed.
testReturn(1)

// Dont print value of i if the value is below zero
func testReturn(*int i) {
    if (i < 0) {
        return
    }
    print i
}
``` 

#### Recursion
You can also call the same function within itself. This will create a recursive loop.
```
// Call the Method recursion with parameter 3
recursion(3)

// The Declaration of the recursion function with an *int as parameter
func recursion(*int i) {
    // Print "Current value: " + i
    print "Current value: " + i
    if (i > 0) {
        // Call itself if i is greater than zero
        recursion(i - 1)
    }
    // Print "end" + i
    print "end" + i
}
```
The output of the code above is as follows.
```
Current value: 3
Current value: 2
Current value: 1
Current value: 0
end0
end1
end2
end3
```

#### Returning something from a Function
You can also return something with the return statement. If you want to return something you need to specify in the declaration of the function what type you want to return.
```
// Function Declaration with return type of *int
func intReturn(*int i) -> *int {
    return i^i
}

// Print the result for Input 2, 3, 4
print intReturn(2)
print intReturn(3)
print intReturn(4)
```
You can also return an unspecific type with '*any'.
```
// This function return an unspecific type
func anyReturn(*int i) -> *any {
    if (i < 0) {
        return i + ""
    }
    if (i < 10) {
        return i + 0.0
    }
    return i
}

// Call the anyReturn function with values of -1, 5, 20
print anyReturn(-1)
print anyReturn(5)
print anyReturn(20)
```

## Import
You can also import other code snippets from other sources or files. These import statements are evaluated before the actual code executor and are only allowed to be written before the actual code. After the first code statement every import will get ignored.
```
import std
// This 'import std' statement imports the std file.

print "Hello World!"
``` 
The std file is a standard library which handles some stuff you want regularly.
```
import std

print "Hello world"
```
You can also just import one sub file of the std library by adding a double point ':' after std and adding the sub name after it.
```
import std:Math
// This just imports the Math sub file of the std library.
``` 
Current std features.
```
std
  - length(*int) -> *int
  - len(*int) -> *int
  - length(*num) -> *int
  - len(*num) -> *int

  std:Casting
    - String(*any) -> *str
    - Character(*any) -> *chr
    - Number(*any) -> *num
    - Integer(*any) -> *int
    - Boolean(*any) -> *bol
    
  std:IO
    - File(*str) -> *str
    - File(*str *bol) -> *str
    - createFile(*str)
    - deleteFile(*str)
    - clearFile(*str)
    - Scanner(*str) -> *str
    - Scanner() -> *str
    
  std:Math
    > PI -> 3.14159265358979323846
    > E -> 2.7182818284590452354
    > DEGREES_TO_RADIANS -> 0.017453292519943295
    > RADIANS_TO_DEGREES -> 57.29577951308232
    > INTEGER_MAX_VALUE ->  2147483647
    > INTEGER_MIN_VALUE -> -2147483647
    
    - round(*num *int) -> *num
    - round(*num *num) -> *num
    - floor(*num) -> *num
    - floor(*num *int) -> *num
    - floor(*num *num) -> *num
    - ceil(*num) -> *num
    - ceil(*num *int) -> *num
    - ceil(*num *num) -> *num
    - multiple(*int *int) -> *int
    - multiple(*num *int) -> *num
    - multiple(*int *num) -> *num
    - multiple(*num *num) -> *num
    - pythagoras(*int *int *int *int) -> *int
    - pythagoras(*num *num *num *num) -> *num
    - toRadians(*num) -> *num
    - toDegree(*num) -> *num
    - max(*int *int) -> *int
    - max(*num *int) -> *num
    - max(*int *num) -> *num
    - max(*num *num) -> *num
    - min(*int *int) -> *int
    - min(*num *int) -> *num
    - min(*int *num) -> *num
    - min(*num *num) -> *num
    - sign(*int) -> *int
    - sign(*num) -> *int
    - clamp(*int *int *int) -> *int
    - clamp(*num *num *num) -> *num
    - isPrime(*int) -> *bol
    
  std:Security
    - hashMD5(*any) -> *str
    - hashSHA1(*any) -> *str
    - hashSHA256(*any) -> *str
    - hash(*any) -> *str
    - encode(*str) -> *str
    - decode(*str) -> *str
    
  std:String
    - length(*str) -> *int
    - len(*str) -> *int
    - toUpperCase(*str) -> *str
    - upperCase(*str) -> *str
    - toLowerCase(*str) -> *str
    - LowerCase(*str) -> *str
    - trim(*str) -> *str
    
  std:Benchmark
    > time
    - startBenchmark
    - stopBenchmark
```

## Strings
There are some special Operations on strings you can use. To execute them use the '.' after the string or the string variable to access them.
To get the length of the String use '.length()'. You can also trim the String with '.trim()' to remove any excess spaces on each end of the String.
You can also Change a String to all Uppercase and or Lowercase. For this use '.toUpperCase()' and 'toLowerCase.()'.
```
var length = "hello".length()
var trim = "    hello    ".trim()

var toLowerCase = "Hello".toLowerCase()
var toUpperCase = "Hello".toUpperCase()

print length
print trim

print toLowerCase
print toUpperCase
```
