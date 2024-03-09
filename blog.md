## Input #1

### Design Process

- require 2 additional classes (Device & Satellite)
- Satellite and Device deal with the common functionality between all types,
  for example getters and setters
- Individual Satellites will deal with there individual movemnent

### Reflection

- pre easy so far, nothing too difficult
- have not needed to implement any other specific classes other than above 3

## Input #2

### Design Process

- created individual classes of satellites to implement movement of satellites
- created new interface Entity, which satellite and device implement in order
  to incorporate getInfo for either one of the classes based just on Id
- abstract classes required for satellite to incorporate the
  interface functions in individual specific classes
- in doing this, we do not have to type cast based on the id provided

### Reflection

- I feel as if there is a better way to implement the entity stuff in order to
  avoid type casting with many if statement checks to find the types
- will have to ask tutor in next lesson

## Input #3

### Design Process

- individual classes for devices created, and devices turned into an abstract
  class to get the range using the entity method
- this allows us to get the range of any entity, whether it is a device or
  satellite
- Feels a bit uncomfortable using object.getInfo().get...??? Violates law of
  demeter possibly

### Reflection

- now it is getting a bit difficult, I feel like the way I access data is not
  ideal and may violate law of demeter
- I need to figure out how I can manage file transfer, whether I keep states of
  the data or something else
