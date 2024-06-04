# Recreating Oriedita Camera...


https://github.com/Minh-Than/RecreateOrieditaCamera/assets/94136126/faf37066-a458-4154-881e-0c972cf20357


...kind of. I'm always intrigued by how [Oriedita](https://github.com/oriedita/oriedita) handles object transformation. Transforming a JPanel with lines on it can be expensive as the whole panel will still render, meaning that there will be a lot of unused areas outside the frame if zoomed in.

Instead, the transformations are done onto the lines themselves, specifically transforming the crease pattern lines and displaying them. This way, the original crease pattern lines' properties (mainly the absolute coordinates) are preserved, and directly drawing transformed lines is generally a lot less expensive when handled properly.

## How Oriedita handles the problem

We'll consider only the idea based on `object2TV()`, a method inside Oriedita's Camera class.

```java
public Point object2TV(Point t_ob) {
    double x1 = t_ob.getX() - camera_position_x;
    double y1 = t_ob.getY() - camera_position_y;
    double x2 = cos_rad * x1 + sin_rad * y1;
    double y2 = -sin_rad * x1 + cos_rad * y1;

    x2 = x2 * camera_mirror;
    x2 = x2 * camera_zoom_x;
    y2 = y2 * camera_zoom_y;
    Point t_tv = new Point(x2 + display_position_x, y2 + display_position_y);
    if (parent != null) {
        t_tv = parent.object2TV(t_tv);
    }
    return t_tv;
}
```

> [!NOTE]  
> The naming scheme of `camera_position_x`, `camera_position_y`, and `display_position_x`, `display_position_y` are quite misleading as, in this case, the camera position more closely represents the "transforming point", and the display position more closely represents the actual "camera/screen click position". The upcoming explanation I'll use camera and display, but think of them as what I believe them to actually be.

### Step 1

To get the object point to display properly, first we'll grab a vector between the object point and the camera position (notated as <x1, y1>).

```java
double x1 = t_ob.getX() - camera_position_x;
double y1 = t_ob.getY() - camera_position_y;
```

### Step 2

This vector allows us to perform rotation/zoom/mirror about the origin of camera position. Below are such transformation:

```java
double x2 = cos_rad * x1 + sin_rad * y1;
double y2 = -sin_rad * x1 + cos_rad * y1;

x2 = x2 * camera_mirror;
x2 = x2 * camera_zoom_x;
y2 = y2 * camera_zoom_y;
```

### Step 3

After the transformation, add the display position to `x2` and `y2`, and return them as coordinate for the display point:

```java
Point t_tv = new Point(x2 + display_position_x, y2 + display_position_y);
...
return t_tv;
```

For method `TV2object()`, the process is the opposite.

### Step 4

How and when to update camera position and display position is also very important. How I understand it is through how we use the mouse.

- `MousePressed()`: the crease pattern should not move or jump at all, so the camera and display positions should be the same. That's because initially [this](#step-1) effectively (and temporarily) moves the object point to a new point, and by adding back the display position [later on](#step-3) will only put it back to the original spot if both display and camera positions are the same. Therefore, we'll simply update both of them to wherever the mouse is.
- `MouseDragged()`: the crease pattern should move about the direction and distance of the display position relative to the camera position. Therefore, we'll only update the display position while dragging and [this step](#step-3) will apply properly.

### Step 5

Now just grab those transformed lines and draw in `paintComponent()`, which is kinda laggy for some reason, so I'm not exactly finished with this project yet.

Update: Switching to Intellij IDEA apparently solves that issue
