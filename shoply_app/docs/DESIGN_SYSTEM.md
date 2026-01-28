# 🎨 Shoply Design System Guide

**מחברת:** Avigail  
**אבן דרך:** 1.5  
**תאריך:** 28.01.2026

---

## 📚 תוכן עניינים

1. [Spacing - מרווחים](#spacing)
2. [Colors - צבעים](#colors)
3. [Typography - טיפוגרפיה](#typography)
4. [Shapes - עיגולי פינות](#shapes)
5. [Components - קומפוננטות](#components)
6. [דוגמאות שימוש](#examples)

---

## 🔲 Spacing - מרווחים

### שימוש במרווחים

**תמיד השתמשי ב-`ShoplySpacing` במקום ערכים קשיחים:**

```kotlin

// ❌ לא נכון
Modifier.padding(16.dp)

// ✅ נכון
Modifier.padding(ShoplySpacing.medium)
```

### ערכים זמינים

| שם | ערך | שימוש |
|---|---|---|
| `none` | 0dp | ללא מרווח |
| `extraSmall` | 4dp | מרווח מינימלי |
| `small` | 8dp | בין אלמנטים קטנים |
| `medium` | 16dp | **מרווח סטנדרטי** |
| `large` | 24dp | בין קבוצות |
| `extraLarge` | 32dp | מרווח גדול |
| `huge` | 48dp | מרווח ענק |

### דוגמאות

```kotlin
Column(
    modifier = Modifier.padding(ShoplySpacing.large)
) {
    Text("כותרת")
    Spacer(Modifier.height(ShoplySpacing.medium))
    Text("תוכן")
}
```

---

## 🎨 Colors - צבעים

### צבעי מותג

```kotlin
// ✅ השתמשי ב-MaterialTheme
Text(
    text = "Shoply",
    color = MaterialTheme.colorScheme.primary  // ירוק
)

Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
)
```

### צבעים זמינים

#### Primary (ירוק)
- `primary` - ירוק ראשי (#2E7D32)
- `onPrimary` - טקסט על ירוק (לבן)
- `primaryContainer` - רקע ירוק בהיר
- `onPrimaryContainer` - טקסט על רקע בהיר

#### Semantic Colors
- `error` - אדום שגיאות
- `onError` - טקסט על אדום
- `background` - רקע לבן
- `surface` - משטחים לבנים
- `outline` - קווי מתאר

### ❌ אל תשתמשי ישירות

```kotlin
// ❌ לא נכון
Text(color = Color(0xFF2E7D32))

// ✅ נכון
Text(color = MaterialTheme.colorScheme.primary)
```

---

## 🔤 Typography - טיפוגרפיה

### שימוש נכון

```kotlin
// ✅ תמיד דרך MaterialTheme
Text(
    text = "כותרת",
    style = MaterialTheme.typography.headlineMedium
)
```

### סגנונות זמינים

#### כותרות גדולות (Display)
- `displayLarge` - 57sp - כותרת ענקית
- `displayMedium` - 45sp
- `displaySmall` - 36sp

#### כותרות (Headline)
- `headlineLarge` - 32sp
- `headlineMedium` - 28sp - **כותרת מסך**
- `headlineSmall` - 24sp

#### תתי כותרות (Title)
- `titleLarge` - 22sp
- `titleMedium` - 16sp - **כותרת כרטיס**
- `titleSmall` - 14sp

#### טקסט גוף (Body)
- `bodyLarge` - 16sp - **טקסט רגיל**
- `bodyMedium` - 14sp
- `bodySmall` - 12sp

#### תוויות (Label)
- `labelLarge` - 14sp - **טקסט כפתור**
- `labelMedium` - 12sp
- `labelSmall` - 11sp

### דוגמה למסך

```kotlin
Column {
    Text(
        text = "מסך הבית",
        style = MaterialTheme.typography.headlineMedium
    )
    Text(
        text = "תיאור המסך",
        style = MaterialTheme.typography.bodyLarge
    )
}
```

---

## 🔘 Shapes - עיגולי פינות

### שימוש

```kotlin

Card(
    shape = RoundedCornerShape(ShoplyRadius.medium)
)

// או דרך Theme
Card(
    shape = MaterialTheme.shapes.medium
)
```

### ערכים זמינים

| שם | ערך |
|---|---|
| `none` | 0dp |
| `small` | 4dp |
| `medium` | 8dp |
| `large` | 12dp |
| `extraLarge` | 16dp |
| `round` | 24dp |

---

## 🧩 Components - קומפוננטות מעודכנות

### ShoplyButton

```kotlin

ShoplyButton(
    text = "לחץ כאן",
    onClick = { /* פעולה */ },
    size = ShoplyButtonSize.Medium,  // Small/Medium/Large
    enabled = true
)
```

### ShoplyTextField

```kotlin

ShoplyTextField(
    value = text,
    onValueChange = { text = it },
    label = "שם משתמש",
    isPassword = false,
    isError = false,
    supportingText = "טקסט עזר"
)
```

---

## 💡 Examples - דוגמאות שימוש

### מסך פשוט

```kotlin
@Composable
fun MyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ShoplySpacing.large)
    ) {
        // כותרת
        Text(
            text = "שם המסך",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(Modifier.height(ShoplySpacing.medium))
        
        // תוכן
        Text(
            text = "תיאור",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(Modifier.height(ShoplySpacing.large))
        
        // כפתור
        ShoplyButton(
            text = "פעולה",
            onClick = { /* ... */ }
        )
    }
}
```

### כרטיס

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(ShoplySpacing.medium),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
) {
    Column(
        modifier = Modifier.padding(ShoplySpacing.medium)
    ) {
        Text(
            text = "כותרת",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(ShoplySpacing.small))
        Text(
            text = "תוכן",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
```

---

## 📌 כללי אצבע

### ✅ עשי
- השתמשי ב-`ShoplySpacing` לכל המרווחים
- השתמשי ב-`MaterialTheme.colorScheme` לכל הצבעים
- השתמשי ב-`MaterialTheme.typography` לכל הטקסטים
- השתמשי ב-`MaterialTheme.shapes` לעיגולים

### ❌ אל תעשי
- לא להשתמש ב-`16.dp` ישירות
- לא להשתמש ב-`Color(0xFF...)` ישירות
- לא להשתמש ב-`TextStyle(fontSize = ...)` ישירות
- לא להמציא ערכים חדשים

---



**מסמך זה נוצר:** 28.01.2026  
**אבן דרך:** 1.5 - Design System