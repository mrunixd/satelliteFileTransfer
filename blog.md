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
- possibly make a new Entity class instead of using the one they gave which keeps
  all the details

## Input #4

### Design Process

- major refactoring of code
- entity is now an abstract class instead of interface as many of the functions in
  device and satellite are very similar (also satellites and devices are entities)
- individual device subclasses may not be important, I am leaving for now incase I
  need it later
- this allowed me to get rid of some repetitive code, also by creating entity class,
  I am able to access information about the entity much easier rather than the
  getEntityInfo method

### Reflection

- at this stage I was quite stuck as I felt that my code was very messy and all
  over the place, but now it is slightly better
- I definitely needed to plan for the next stage better so by simplifying the code
  now, it makes the future slightly easier

## Input #5

### Design Process

- I am combatting file transfer by keeping a state of all the files that are
  currently being transferred
- this means that at each simulation this state is updated to match the file transfer
- by adding a new class FileTransfer and including a FileTransferState list in
  blackout controller, I can manage what is happening betwen files
- the file transfer carries info on the sender, reciever, bytes sent and size of the
  file

### Reflection

- I have not completed the implementation but I have questions whether my
  FileTransferState list should be kept in blackout or as its own seperate class
- but to not over complicate it for now, I have kept it in the controller and
  can change it later if needed

## Input #6

### Questions for tutor

- Look at switch case thing we talked about in tute in my code
- Check why my remove function is not working, list.remove(obj)
- Is it okay to have Device and Satellite inheriting entity and the sub
  classes inheriting Device and Satellite
- SendFile function, excessive use of if statements and instanceof
- What is the point of EntityInfoResponse, if I can make my entity class
- Should I have a seperate class for my state?
