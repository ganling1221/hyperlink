
The authoring tool will take two video, and create a file of the primary video eg(AIFilmOne_metadata.txt) and by dragging the mouse over the screen, we can draw a bounding box, and by clicking connect button, we connect the bounding box to the secondary video of that specific frame we are at. Also, this information get written on file simultaneously. For each frame, we can do multiple bounding box, but only the current box will be shown on screen, all the created bounding box is displayed in the bounding box list on screen. We will write to the same metadata file as long as we do not change the primary video, hence a primary video can be linked to multiple secondary video.

By clicking save file button, we closes the current file we are writing and is free to load another primary video. 

BOUDNING BOX FORMAT:
BOX_ONE
frame:3
x:110
y:110
w:10
h:10
path:AIFilmTwo/AIFilmTwo
subFrame:12


