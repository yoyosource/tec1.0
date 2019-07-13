# tec
'**tec**' is a new programming languge that is user friendly as you can just write your Code. You do not have to mess with any **main** functions if you do not want to. If you want to start with tec you do not need to have any knowledge of any programming languages what so ever.
To change to a new language you can use this a ground basis to change to Java, Swift, Python, JavaScript, Go. If you have any knowledge in Java, Swift, Python, Go or JavaScript you should not have any trouble to write programms in **tec** if you know the basics.

## Getting started
Create a file with the suffix **.tec**. Open the file and now you can write your first code. Start with a 'Hello World' programm.
```
print "Hello World"
```
If you execute this Program it will output Hello World.

## Comments
One line Comments start with '//'. There are also Block comment which start with '/\*' at the start of a line and end with '\*/' at the end of a line.
```
// This prints "Hello World"
print "Hello World"

/*Here are some prints for some formating
also this system writes Hello World 3 Times*/
print ""
print "Hello World"
print "Hello World"
print "Hello World"
print ""
```

## Datatypes
There are 5 Datatypes. The Datatypes are chr for Character, bol for Boolean, num for Float, int for Integer and str for String.
```
*chr '<One Character here>'
*bol <true/false>
*num <Floating point number>
*int <Non Floating point Number or Hex Number with #>
*str "One or More Characters" or 'Many Character'
```

## Variables
There are 2 types of Variables, constant ones and dynamic ones. Dynamic variables are changeable and can be reassigned.
```
// This Variable is an Integer Variable '0'
var int = 0
var int2 = #1

// This Variable is a Float Variable '0.0'
var num = 0.0

// This Variable is a Character Variable 'c'
var chr = 'c'

// This Variable is a Boolean Variable 'true'
var bol = true

// This Variable is a String Variable 'Hello World'
var str = "Hello World"
var str2 = 'Hello World'

// To Create  a Constant instead of using 'var' use 'let'
let str = "HELP"
```

## Advanced Printing
To print a Value of a Variable you can write the Variable name behind the print Statement. The print Statement can also Calculate stuff and you can add Calculations and variables together.
```
// This print will calculate the sum of the value of 'i' and 1
var i = 1
print i + 1

// This print will output "c2"
print "c" + (i+1)

// You can also calculate the sum of 2 Variables
var j = 3
print j + i

// To put String together you can also use +
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
If you want to do choose what you do use 'if'
```
// Use an Boolean Expression in the Brackets of the is Statement.
if (true) {

}

if (1 == 1) {

}

if (true == false) {

}

if (true || false) {

}
```