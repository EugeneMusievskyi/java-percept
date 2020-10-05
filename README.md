# PERCEPT

It's backend of web service for finding similar images using dHash algorithm. 

If similar images contains in storage (user can choose match percentage) the program will return these images, otherwise it will be saved asynchronously to the storage.
```
 Image --> count hash ----> finding similar images in persistent storage  ------> sending response to client -------------------------------------------------->
                                                                          \
                                                                           \                                                                                                                                                         
                                                                            \---writing image to disk --------> writing image to persistent storage ------>
```

Client can send batch of images to save to storage. Each image processes in different threads.
```
                                   image 1 -----> counting hash --------------> writing to persistent srorage --\ 
                                 /               \                        /                                      \                   
                                /                 --- writing to disk ---/                                        \
                               /                                                                                   \
- Getting array of images -----                                                                                      -----> sending response to client
                               \                                                                                   /
                                \                                                                                 /
                                 \                                                                               /
                                  image 2 -----> counting hash --------------> writing to persistent storage  --/ 
                                                \                               /
                                                 --- writing to disk ---/
``` 

Endpoints: 
```
POST /image/batch
content-type: multipart/form-data
images: MultipartFile[]

Loads array of files, counts hash, saves image to disk and persistent srorage.

POST /image/search?threshold=?
content-type: multipart/form-data
image: MultipartFile

Response: [
    {
        id: "92c73b0f-77d6-41b9-be87-1e0ebf20be31",
        image: "http://127.0.0.1:8080/files/92c73b0f-77d6-41b9-be87-1e0ebf20be31.jpg",
        match: 96.1265
    }
]

Loads the file you want to search in persistent storage with the specified minimum match percentage (threshold). Threshold must be within (0, 1]. Default value is 0.9. If no similar images are found, you must saves it to disk and adds the record to persistent storage.

DELETE /image/{id}

Deletes the specified image from persistent storage and from the hard disk

DELETE /image/purge

Deletes all images from the hard disk and from persistent storage
```
