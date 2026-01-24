package com.shoply.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shoply.app.ui.theme.ShoplyAppTheme

@Preview(showBackground = true)
@Composable
private fun ShoplyImagePreview() {
    ShoplyAppTheme {
        Column {
            // תמונה תקינה (רשת)
            ShoplyImage(
                imageUrl = "https://picsum.photos/300/200",
                contentDescription = "Demo image",
                modifier = Modifier.size(width = 300.dp, height = 200.dp)
            )

            Spacer(Modifier.height(16.dp))

            // ריק → יראה אייקון Image
            ShoplyImage(
                imageUrl = "",
                contentDescription = "Empty image",
                modifier = Modifier.size(width = 300.dp, height = 200.dp)
            )
        }
    }
}
