package com.example.ffridge.data.local

import com.example.ffridge.domain.model.Recipe

object LocalRecipeDataSource {

    fun getPopularRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = 1,
                title = "Cơm Chiên Trứng",
                ingredients = listOf("Cơm", "Trứng", "Hành", "Tỏi", "Dầu ăn", "Nước tương"),
                description = "1. Đun nóng chảo với dầu ăn\n2. Phi thom tỏi băm\n3. Cho trứng vào đánh tan\n4. Thêm cơm và đảo đều\n5. Nêm nước tương, hành",
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
                description = "1. Rửa sạch cá, cắt khúc vừa ăn\n2. Đun sôi nước, cho me vào nấu\n3. Thêm cà chua, thơm, đậu\n4. Cho cá vào, nêm nếm vừa ăn\n5. Tắt bếp, cho ngò rau thơm",
                imageUrl = "https://images.unsplash.com/photo-1559847844-5315695dadae?w=500",
                cookingTime = "25 phút",
                difficulty = "Trung bình"
            ),
            Recipe(
                id = 4,
                title = "Gà Xào Xả Ớt",
                ingredients = listOf("Thịt gà", "Sả", "Ớt", "Tỏi", "Hành tây", "Nước mắm", "Đường"),
                description = "1. Thái gà miếng vừa, ướp gia vị\n2. Sả thái lát, ớt thái khúc\n3. Đun nóng chảo, phi thơm tỏi\n4. Xào gà đến săn\n5. Cho sả, ớt vào xào thơm",
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
            ),
            Recipe(
                id = 6,
                title = "Canh Rau Củ",
                ingredients = listOf("Cà rốt", "Khoai tây", "Bắp cải", "Thịt", "Hành", "Nước mắm"),
                description = "1. Gọt và thái củ thành miếng vừa\n2. Luộc thịt, vớt ra thái lát\n3. Nấu nước dùng với củ\n4. Thêm bắp cải, thịt\n5. Nêm nếm, cho hành",
                imageUrl = "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=500",
                cookingTime = "30 phút",
                difficulty = "Trung bình"
            ),
            Recipe(
                id = 7,
                title = "Thịt Kho Trứng",
                ingredients = listOf("Thịt ba chỉ", "Trứng", "Nước dừa", "Nước mắm", "Đường", "Hành tím"),
                description = "1. Luộc trứng, bóc vỏ\n2. Thái thịt miếng vuông\n3. Ướp thịt với gia vị\n4. Kho thịt với nước dừa\n5. Cho trứng vào kho cùng",
                imageUrl = "https://images.unsplash.com/photo-1626804475297-41608ea09aeb?w=500",
                cookingTime = "45 phút",
                difficulty = "Trung bình"
            ),
            Recipe(
                id = 8,
                title = "Rau Xào Tỏi",
                ingredients = listOf("Rau muống", "Tỏi", "Dầu ăn", "Muối", "Nước mắm"),
                description = "1. Rửa sạch rau, cắt khúc\n2. Băm nhỏ tỏi\n3. Đun nóng chảo, phi tỏi\n4. Cho rau vào xào nhanh tay\n5. Nêm muối, nước mắm",
                imageUrl = "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=500",
                cookingTime = "10 phút",
                difficulty = "Dễ"
            ),
            Recipe(
                id = 9,
                title = "Canh Chua Tôm",
                ingredients = listOf("Tôm", "Cà chua", "Dứa", "Đậu bắp", "Ngò gai", "Me", "Đường"),
                description = "1. Rửa tôm, cắt râu\n2. Nấu nước me với đường\n3. Cho cà chua, dứa vào\n4. Thêm tôm nấu chín\n5. Nêm nếm, thêm rau thơm",
                imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500",
                cookingTime = "20 phút",
                difficulty = "Dễ"
            ),
            Recipe(
                id = 10,
                title = "Bò Xào Rau Củ",
                ingredients = listOf("Thịt bò", "Cà rốt", "Súp lơ", "Hành tây", "Tỏi", "Nước tương", "Dầu hào"),
                description = "1. Thái thịt bò mỏng, ướp\n2. Cắt rau củ miếng vừa\n3. Xào thơm tỏi, hành\n4. Xào bò nhanh tay\n5. Cho rau củ vào xào cùng",
                imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500",
                cookingTime = "25 phút",
                difficulty = "Trung bình"
            )
        )
    }

    fun getRecipeById(id: Int): Recipe? {
        return getPopularRecipes().find { it.id == id }
    }
}
