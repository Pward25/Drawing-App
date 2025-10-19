# Drawing App

## Overview

This Android drawing application is designed to help me develop practical skills in mobile app development, specifically in building feature-rich Android applications using Kotlin. Through this project, I'm learning about custom views, database management, background threading with coroutines, and implementing undo/redo functionality. The goal is to create a functional drawing tool that demonstrates clean architecture, efficient resource management, and user-friendly design patterns.

## App Description

Drawing App is a simple yet feature-rich drawing application for Android that allows users to create, save, and manage digital drawings.
[Software Demo Video](https://youtu.be/mt-FSJkq6iM)

**How to use the app:**

1. **Launch the app** - The main screen displays a gallery of previously saved drawings in a grid layout
2. **Create a new drawing** - Tap the floating action button (+) at the bottom-right to start drawing
3. **Draw on canvas** - Use your finger to draw freely on the white canvas
4. **Adjust brush size** - Use the seekbar at the top to change brush thickness (2-50 pixels)
5. **Change colors** - Tap any of the five color buttons (Black, Red, Blue, Green, Yellow) to select your drawing color
6. **Undo/Redo** - Use the Undo and Redo buttons to navigate through your drawing history (stores up to 20 states)
7. **Clear canvas** - Tap Clear to erase the entire drawing and start fresh
8. **Save drawing** - Tap Save to store your drawing to the database
9. **Delete drawing** - Tap Delete to remove the current drawing
10. **View saved drawings** - Return to the main screen to see all saved drawings as thumbnails

## Development Environment

**Tools:**
- Android Studio (latest version)
- Android Virtual Device (AVD) emulator

**Programming Language & Libraries:**
- Kotlin
- AndroidX libraries (AppCompat, RecyclerView, Material Design)
- Coroutines for asynchronous operations
- SQLite for local data persistence
- Android Graphics API for canvas drawing

## Useful Websites

* [Android Developers Documentation](https://developer.android.com/docs)
* [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-overview.html)
* [RecyclerView Guide](https://developer.android.com/guide/topics/ui/layout/recyclerview)
* [SQLite Android Tutorial](https://developer.android.com/training/data-storage/sqlite)
* [Custom Views in Android](https://developer.android.com/guide/topics/ui/custom-components)

## Future Work

* Implement brush shape options (circle, square, eraser)
* Add color picker for custom colors instead of preset colors only
* Implement layer system for complex drawings
* Add drawing filters and effects
* Export drawings as image files (PNG, JPEG)
* Optimize bitmap memory usage for large drawings
* Add drawing rotation and scaling tools
* Implement cloud sync to backup drawings
* Add gesture support (pinch to zoom, two-finger pan)
* Create a favorites/favorites system for frequently used drawings