# Rigid Body Physics In 2D

Just a small (impulse-based) rigid body physics engine in 2D :)
It includes:

- rectangle-rectangle collisions (Separating Axis Theorem)
- rectangle-circle collisions
- circle-circle collisions
- collision resolution (normal impulse + friction impulse)
- AABB optimization

The engine is far from complete, but I think it's fine for 1.5 days of coding :)

## Demo
#### (takes a few seconds to load by GitHub)

![Physics Engine Demo](assets/demo.gif)

**NOTE:** with a lot of dynamic objects in a scene, the simulation tends to get unstable. There's no
sophisticated collision resolution algorithm running in the background.
