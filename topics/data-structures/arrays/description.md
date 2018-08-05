Arrays
======


Description
-----------

A variable is useful to store one value. To store several values of the same type, it is possible to use an array.

An array is a data structure that contains a group of elements. Typically these elements are all of the same data type, such as integer or string.

Arrays are commonly used in computer programs to organize data so that a related set of values can be easily sorted or searched.

Each element of an array is accessed by its index.

The access time to an element by index is constant, regardless of the desired element. This is explained by the fact that array elements are contiguous in memory. Thus, it is possible to calculate the memory address of the element to be accessed, from the base address of the array and the index of the element. Access is immediate, as it would be for a single variable.

This advantage is also one of the limits of such a structure. An array is represented in memory in the form of contiguous cells, insert and delete operations element are impossible, unless you create a new array, larger or smaller (depending on the operation). It is then necessary to copy all elements of the original array into the new one, and then release the memory space allocated to the old array. So that's a lot of operations and require some languages providing such opportunities to implement their own array, not in the traditional form (adjacent cells), but using a linked list, or a combination of both structures to improve performance.

<p class="md-right">
Source: Wikipedia (<a href="http://creativecommons.org/licenses/by-sa/3.0" target="_blank">license</a>)
</p>


External Resources
------------------

- [Associative array](https://en.wikipedia.org/wiki/Associative_array)
- [Multidimensional array](https://en.wikipedia.org/wiki/Array_data_structure#Multidimensional_arrays)
- [Two dimensional array](https://processing.org/tutorials/2darray/)
