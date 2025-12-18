package com.example.ffridge.data.local

import com.example.ffridge.domain.model.Recipe

object LocalRecipeDataSource {

    fun getPopularRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = 1,
                title = "Cơm Chiên Trứng",
                ingredients = listOf("Cơm", "Trứng", "Hành", "Tỏi", "Dầu ăn", "Nước tương"),
                description = "1. Đun nóng chảo với dầu ăn\n2. Phi thơm tỏi băm\n3. Cho trứng vào đánh tan\n4. Thêm cơm và đảo đều\n5. Nêm nước tương, hành lá",
                imageUrl = "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=500",
                cookingTime = "15 phút",
                difficulty = "Dễ"
            ),
            Recipe(
                id = 2,
                title = "Mì Xào Thịt Bò",
                ingredients = listOf("Mì trứng", "Thịt bò", "Rau cải", "Hành tây", "Tỏi", "Nước tương", "Dầu hào"),
                description = "1. Luộc mì qua nước sôi, vớt ra\n2. Thái thịt bò mỏng, ướp gia vị\n3. Xào thơm tỏi, hành tây\n4. Xào thịt bò đến chín\n5. Cho mì vào xào cùng, nêm nếm",
                imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=500",
                cookingTime = "20 phút",
                difficulty = "Dễ"
            ),
            Recipe(
                id = 3,
                title = "Canh Chua Cá",
                ingredients = listOf("Cá", "Cà chua", "Thơm", "Đậu bắp", "Ngò", "Me", "Đường", "Muối"),
                description = "1. Rửa sạch cá, cắt khúc vừa ăn\n2. Đun sôi nước, cho me vào nấu\n3. Thêm cà chua, thơm, đậu bắp\n4. Cho cá vào, nêm nếm vừa ăn\n5. Tắt bếp, cho ngò rau thơm",
                imageUrl = "https://images.unsplash.com/photo-1559847844-5315695dadae?w=500",
                cookingTime = "25 phút",
                difficulty = "Trung bình"
            ),
            Recipe(
                id = 4,
                title = "Gà Xào Xả Ớt",
                ingredients = listOf("Thịt gà", "Sả", "Ớt", "Tỏi", "Hành tây", "Nước mắm", "Đường"),
                description = "1. Thái gà miếng vừa, ướp gia vị\n2. Sả thái lát, ớt thái khúc\n3. Đun nóng chảo, phi thơm tỏi\n4. Xào gà đến săn lại\n5. Cho sả, ớt vào xào thơm",
                imageUrl = "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=500",
                cookingTime = "20 phút",
                difficulty = "Dễ"
            ),
            Recipe(
                id = 5,
                title = "Trứng Chiên",
                ingredients = listOf("Trứng", "Hành lá", "Muối", "Tiêu", "Dầu ăn"),
                description = "1. Đánh tan trứng với muối, tiêu\n2. Thêm hành lá thái nhỏ\n3. Đun nóng chảo với dầu\n4. Đổ trứng vào chiên\n5. Lật mặt cho vàng đều",
                imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=500",
                cookingTime = "10 phút",
                difficulty = "Dễ"
            )
        )
    }

    fun getRecipeById(id: Int): Recipe? {
        return getPopularRecipes().find { it.id == id }
    }
}
