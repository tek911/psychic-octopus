package com.nexuspaths.game.graphics

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Particle system for visual effects like explosions and energy collection
 */
class ParticleSystem {

    private val particles = mutableListOf<Particle>()
    private val particlePool = mutableListOf<Particle>()
    private val maxParticles = 500

    /**
     * Create an explosion effect at a position
     */
    fun createExplosion(x: Float, y: Float, color: Int, count: Int = 20) {
        repeat(count) {
            val angle = Random.nextDouble(0.0, 2 * Math.PI)
            val speed = Random.nextFloat() * 200f + 100f
            val velocity = PointF(
                (cos(angle) * speed).toFloat(),
                (sin(angle) * speed).toFloat()
            )

            val particle = getParticle().apply {
                position.set(x, y)
                this.velocity.set(velocity.x, velocity.y)
                this.color = color
                this.alpha = 255
                this.size = Random.nextFloat() * 8f + 4f
                this.lifetime = Random.nextFloat() * 0.8f + 0.4f
                this.age = 0f
                this.type = ParticleType.EXPLOSION
            }
            particles.add(particle)
        }
    }

    /**
     * Create energy collection particles
     */
    fun createEnergyCollect(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: Int,
        count: Int = 10
    ) {
        repeat(count) {
            val particle = getParticle().apply {
                position.set(startX, startY)
                target.set(endX, endY)
                this.color = color
                this.alpha = 255
                this.size = Random.nextFloat() * 6f + 3f
                this.lifetime = Random.nextFloat() * 0.5f + 0.5f
                this.age = 0f
                this.type = ParticleType.ENERGY_COLLECT
                this.velocity.set(
                    Random.nextFloat() * 40f - 20f,
                    Random.nextFloat() * 40f - 20f
                )
            }
            particles.add(particle)
        }
    }

    /**
     * Create a trail effect
     */
    fun createTrail(x: Float, y: Float, color: Int) {
        val particle = getParticle().apply {
            position.set(x, y)
            this.color = color
            this.alpha = 200
            this.size = 6f
            this.lifetime = 0.3f
            this.age = 0f
            this.type = ParticleType.TRAIL
            velocity.set(0f, 0f)
        }
        particles.add(particle)
    }

    /**
     * Update all particles
     */
    fun update(deltaTime: Float) {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.update(deltaTime)

            if (particle.age >= particle.lifetime) {
                returnParticle(particle)
                iterator.remove()
            }
        }
    }

    /**
     * Draw all particles
     */
    fun draw(canvas: Canvas, paint: Paint) {
        particles.forEach { it.draw(canvas, paint) }
    }

    /**
     * Get a particle from pool or create new one
     */
    private fun getParticle(): Particle {
        return if (particlePool.isNotEmpty()) {
            particlePool.removeAt(particlePool.size - 1)
        } else {
            Particle()
        }
    }

    /**
     * Return particle to pool
     */
    private fun returnParticle(particle: Particle) {
        if (particlePool.size < maxParticles) {
            particle.reset()
            particlePool.add(particle)
        }
    }

    /**
     * Clear all particles
     */
    fun clear() {
        particles.forEach { returnParticle(it) }
        particles.clear()
    }

    /**
     * Get particle count
     */
    fun getParticleCount(): Int = particles.size
}

/**
 * Individual particle
 */
class Particle {
    val position = PointF()
    val velocity = PointF()
    val target = PointF()
    var color: Int = 0
    var alpha: Int = 255
    var size: Float = 5f
    var lifetime: Float = 1f
    var age: Float = 0f
    var type: ParticleType = ParticleType.EXPLOSION

    fun update(deltaTime: Float) {
        age += deltaTime

        when (type) {
            ParticleType.EXPLOSION -> {
                // Apply gravity and friction
                velocity.y += 500f * deltaTime
                velocity.x *= 0.98f
                velocity.y *= 0.98f

                position.x += velocity.x * deltaTime
                position.y += velocity.y * deltaTime

                // Fade out
                alpha = (255 * (1f - age / lifetime)).toInt().coerceIn(0, 255)
                size = size * 0.98f
            }

            ParticleType.ENERGY_COLLECT -> {
                // Move towards target
                val progress = age / lifetime
                val dx = target.x - position.x
                val dy = target.y - position.y

                velocity.x += dx * deltaTime * 5f
                velocity.y += dy * deltaTime * 5f

                position.x += velocity.x * deltaTime
                position.y += velocity.y * deltaTime

                // Fade out near the end
                alpha = if (progress > 0.7f) {
                    (255 * (1f - (progress - 0.7f) / 0.3f)).toInt().coerceIn(0, 255)
                } else {
                    255
                }
            }

            ParticleType.TRAIL -> {
                // Simple fade out
                alpha = (200 * (1f - age / lifetime)).toInt().coerceIn(0, 255)
                size = size * 0.95f
            }
        }
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = color
        paint.alpha = alpha
        paint.style = Paint.Style.FILL

        when (type) {
            ParticleType.EXPLOSION -> {
                canvas.drawCircle(position.x, position.y, size, paint)
            }
            ParticleType.ENERGY_COLLECT -> {
                canvas.drawCircle(position.x, position.y, size, paint)
                // Draw glow
                paint.alpha = (alpha * 0.3f).toInt()
                canvas.drawCircle(position.x, position.y, size * 2f, paint)
            }
            ParticleType.TRAIL -> {
                canvas.drawCircle(position.x, position.y, size, paint)
            }
        }
    }

    fun reset() {
        position.set(0f, 0f)
        velocity.set(0f, 0f)
        target.set(0f, 0f)
        color = 0
        alpha = 255
        size = 5f
        lifetime = 1f
        age = 0f
    }
}

enum class ParticleType {
    EXPLOSION,
    ENERGY_COLLECT,
    TRAIL
}
