# Nexus Paths - How to Play

## Game Overview

Nexus Paths is a fast-paced hexagonal match-3 puzzle game where you connect colored nodes to charge elemental cores and trigger powerful abilities. You have 60 seconds to achieve the highest score possible!

---

## 🎯 The Matching System Explained

### **CRITICAL: How Matching Actually Works**

Many players initially struggle because the matching rules are different from traditional match-3 games. Here's what you need to know:

#### ✅ Valid Matches REQUIRE:

1. **Edge-Adjacent Connections Only**
   - Nodes must touch along their hexagonal edges
   - **Diagonal/corner touching does NOT count as adjacent**
   - Think of it like you're drawing a path where each hex shares a full edge with the next

2. **Minimum 3 Connected Nodes**
   - You need at least 3 nodes of the same color connected together
   - 2 matching nodes next to each other is **NOT enough** for a match

3. **Same Color Group**
   - All nodes in the connected group must be the same color
   - Wildcards (white) can match with any color and connect groups

#### ❌ Common Mistakes:

```
❌ WRONG - Only 2 nodes (need at least 3)
   🔴
   🔴

❌ WRONG - Corner touching (not edge-adjacent)
   🔴 🔵
      🔴

❌ WRONG - Different colors (must be same color or wildcard)
   🔴 🔵 🔴

✅ CORRECT - 3+ edge-adjacent nodes of same color
   🔴
   🔴
   🔴

✅ CORRECT - L-shape pattern (all edge-adjacent)
   🔴 🔴
   🔴

✅ CORRECT - Wildcard connecting two groups
   🔴 ⚪ 🔴
```

### Visual Guide to Adjacency

In a hexagonal grid, each node has **exactly 6 neighbors** (like a clock):
```
      [12:00]
[10:00]  🔴  [2:00]
      \  |  /
       \ | /
[8:00] — ⭐ — [4:00]
       / | \
      /  |  \
[6:00]   🔴  [4:00]
```

Only nodes at these 6 positions count as adjacent to the center ⭐.

### The Flood-Fill Algorithm

When you tap a node, the game uses a "flood-fill" algorithm:
1. Starts at the tapped node
2. Checks all 6 edge-adjacent neighbors
3. If a neighbor is the same color (or wildcard), add it to the group
4. Repeat step 2-3 for each newly added node
5. If the final group has 3+ nodes, it's a valid match!

This is why **all nodes must form a continuous chain** - the algorithm spreads like water flowing through connected spaces.

---

## 🎮 Basic Controls

**Tap any node** to select and attempt a match:
- If the connected group has 3+ nodes → successful match!
- If less than 3 nodes → no match (try elsewhere)
- Selected nodes glow and scale up
- White border appears around selection

**Drag to select multiple** (if you tap and hold, you can drag to see the selection grow)

---

## 🌈 Nexus Cores & Element Types

Each color represents a different elemental "Nexus Core" with unique abilities:

### 🔴 **RED - Warrior Core**
- **Ability**: Destroys all nodes of one color
- **Strategy**: Save for when the board is cluttered with one color
- **Best used**: To clear obstacles or set up larger combos

### 🔵 **BLUE - Mage Core**
- **Ability**: Shuffles the entire board
- **Strategy**: Use when no good matches are available
- **Best used**: To break out of difficult board states

### 🟣 **PURPLE - Rogue Core**
- **Ability**: Creates 3+ wildcard nodes
- **Strategy**: Wildcards connect any colors together
- **Best used**: To link separated color groups for massive matches

### 🟢 **GREEN - Healer Core**
- **Ability**: Extends game time by 10+ seconds
- **Strategy**: Your lifeline when running low on time
- **Best used**: When you have 10-15 seconds left and good momentum

### 🟡 **YELLOW - Artificer Core**
- **Ability**: Doubles your combo multiplier
- **Strategy**: Activates on high combos for explosive scoring
- **Best used**: When you're in the middle of a combo streak

### ⚪ **WILDCARD Nodes**
- Match with **ANY color**
- Can bridge different color groups
- Created by Purple Rogue Core ability
- Extremely valuable for large matches

---

## 💯 Scoring System

### Base Points
```
Match Size:
3 nodes  = 30 points  (10 per node)
4 nodes  = 40 points  (10 per node)
5 nodes  = 50 points  (10 per node)
6+ nodes = 60+ points (10 per node)
```

### Pattern Multipliers

The game detects special patterns and awards multipliers:

#### ⭐ **STAR Pattern** (3x multiplier)
- Requires: 6+ nodes
- Pattern: One center node touching 5+ matched neighbors
- Visual: Looks like a star/flower shape
- Example:
  ```
      🔴  🔴  🔴
       \ | /
    🔴 — 🔴 — 🔴
       / | \
      🔴  🔴  🔴
  ```

#### 📏 **LINE Pattern** (2x multiplier)
- Requires: 5+ nodes
- Pattern: All nodes in a straight line (same q, r, or s axis)
- Visual: A straight diagonal/vertical/horizontal line
- Example:
  ```
  🔴
   \
    🔴
     \
      🔴
       \
        🔴
         \
          🔴
  ```

#### 🎯 **CLUSTER Pattern** (2.5x multiplier)
- Requires: 7+ nodes
- Pattern: Any large grouped shape (not star or line)
- Visual: Blob-like concentration of nodes

#### 🎲 **REGULAR Match** (1x multiplier)
- 3-4 nodes in any configuration
- No special pattern detected

### Combo System

**Consecutive matches** build your combo multiplier:
```
Combo x1: No combo active
Combo x2: 2 consecutive matches
Combo x3: 3 consecutive matches
Combo x4: 4 consecutive matches
...and so on!
```

**Final Score Calculation:**
```
Score = Base Points × Pattern Multiplier × Combo Multiplier
```

Example:
- 6-node STAR match (60 points × 3x pattern) = 180 points
- At Combo x4 = 180 × 4 = **720 points**
- Yellow Artificer doubles it = **1,440 points from one match!**

**Combo breaks** if you:
- Wait too long between matches (timer expires)
- Tap a non-matching group (less than 3 nodes)
- Let the board fill completely with no moves

---

## ⚡ Charging Cores

### How Cores Charge:
- Each match adds energy to its corresponding colored core
- Larger matches = more energy
- Pattern bonuses add extra energy
- Combos accelerate charging

### Core Levels:
Cores level up as you use them:
- **Level 1**: Starting level
- **Level 2+**: More powerful effects (more wildcards, longer time extensions, etc.)

### Visual Indicators:
- **Empty Circle**: No charge yet
- **Filling Arc**: Partially charged (yellow/orange)
- **Full Circle + Glow**: Ready to activate!
- **White Border**: Tap to activate ability

### Strategic Core Usage:
1. **Save cores** for critical moments (low time, no matches)
2. **Chain abilities**: Use Purple → Yellow for massive combos
3. **Green is insurance**: Don't waste it when time is plentiful
4. **Blue when stuck**: Better to shuffle than break combo
5. **Red for cleanup**: Clear dominant colors blocking good matches

---

## 🎪 Advanced Strategies

### 1. **Color Management**
- Watch which colors dominate the board
- Use Red Warrior to thin out overcrowded colors
- Balance your matches across all colors

### 2. **Wildcard Tactics**
- Save Purple Rogue ability for complex boards
- Use wildcards to bridge large separated groups
- One wildcard can turn two 2-node groups into a 5-node match!

### 3. **Combo Preservation**
- **Move quickly** between matches
- Have your next move planned before finishing current match
- Don't tap uncertain groups - breaks combo!

### 4. **Time Management**
- First 30 seconds: Build cores and score
- 30-20 seconds: Start using cores strategically
- Under 20 seconds: Use Green Healer if charged
- Final 10 seconds: Use ALL remaining charges for points

### 5. **Pattern Hunting**
Visual patterns to look for:
- **Lines along board edges** (easier to spot)
- **Clusters in corners** (tend to form naturally)
- **Wildcard stars** (wildcard + 5 same color around it)

### 6. **Core Combos**
Powerful ability combinations:
- Purple (wildcards) → Yellow (double combo) → Red (clear board) = massive points
- Green (time) → Blue (shuffle) → continue combo = reset advantage
- Multiple Yellows in a row = exponential scoring

---

## 🏆 Scoring Milestones

Track your improvement:
- **Beginner**: 1,000 - 2,000 points
- **Intermediate**: 2,000 - 5,000 points
- **Advanced**: 5,000 - 10,000 points
- **Expert**: 10,000 - 20,000 points
- **Master**: 20,000+ points

---

## ❓ FAQ / Troubleshooting

### "I tapped two matching nodes and nothing happened!"
- You need **minimum 3 nodes** connected together
- 2 is not enough for a match

### "I tapped three nodes of the same color and it didn't match!"
- Check if they're **edge-adjacent** (sharing a full edge)
- Corner-touching doesn't count
- They must form a continuous connected group

### "What counts as 'adjacent' in a hex grid?"
- Only the 6 nodes that share a full edge (see diagram above)
- Diagonals and corners don't count
- Think "touching edges" not "touching corners"

### "How do I know if nodes are connected?"
- Tap and hold - selected nodes will glow
- If the glow spreads to neighbors, they're connected
- White borders show the full connected group

### "My combo broke but I was matching fast!"
- You may have tapped a group with less than 3 nodes
- Invalid match attempts break combos
- Make sure every tap results in a successful match

### "When should I use core abilities?"
- **Red**: When one color dominates (40%+ of board)
- **Blue**: When no good matches visible (stuck)
- **Purple**: Before big combo streaks (creates opportunities)
- **Green**: When under 15 seconds remaining
- **Yellow**: During active combos (x3 or higher)

### "What's the difference between wildcard and regular nodes?"
- **Regular nodes**: Only match their own color
- **Wildcards**: Match with ANY color and connect different colored groups
- Wildcards are much more valuable

---

## 🎨 Visual Indicators Summary

- **Hexagon glow**: Node is selected or part of match
- **White border**: Node is in selected group
- **Scale animation**: Node selected/tapped
- **Rotation + fade**: Match successful (nodes being removed)
- **Particle explosion**: Match completed
- **Energy particles**: Flying to cores after match
- **Core glow**: Ability ready to activate
- **Core arc fill**: Charging progress (empty → full circle)
- **Red timer**: 10 seconds or less remaining (urgency!)
- **Orange multiplier**: Active combo (top of screen)

---

## 🎯 Quick Start Summary

1. **Tap 3+ edge-adjacent nodes** of the same color
2. **Build combos** by matching quickly and consecutively
3. **Charge cores** with matches, activate when glowing
4. **Use abilities strategically** based on board state and time
5. **Hunt for pattern bonuses** (stars, lines, clusters)
6. **Maximize points** = Large matches × Patterns × Combos × Abilities

---

## 💡 Pro Tips

- 👀 **Scan the board before tapping** - plan 2-3 moves ahead
- ⚡ **Speed matters** - quick matches preserve combos
- 🎯 **Quality over quantity** - one 6-node star beats three 3-node matches
- 🌈 **Color balance** - don't ignore any color completely
- ⏱️ **Time awareness** - use Green Healer at 15s, not 45s
- 🎪 **Combo protection** - never tap uncertain groups during combos
- ♻️ **Board refresh** - if stuck, use Blue Mage (don't wait)
- 🎨 **Wildcard value** - Purple Rogue is one of the most powerful abilities

---

Good luck, and may your paths through the Nexus be ever prosperous! 🌟
