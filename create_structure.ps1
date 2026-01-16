# Script tạo cấu trúc thư mục cho Android App - Clean Architecture
# Chạy script: .\create_structure.ps1

Write-Host "🚀 Đang tạo cấu trúc thư mục Clean Architecture..." -ForegroundColor Green

$basePackage = "app\src\main\java\com\kma\forumkma"
$testPackage = "app\src\test\java\com\kma\forumkma"
$androidTestPackage = "app\src\androidTest\java\com\kma\forumkma"

# Danh sách thư mục cần tạo
$directories = @(
    # Core layer
    "$basePackage\core\base",
    "$basePackage\core\di",
    "$basePackage\core\utils",
    "$basePackage\core\network",
    "$basePackage\core\network\interceptors",
    "$basePackage\core\local",
    "$basePackage\core\local\dao",
    
    # Data layer
    "$basePackage\data\remote\dto\auth",
    "$basePackage\data\remote\dto\post",
    "$basePackage\data\remote\dto\comment",
    "$basePackage\data\remote\dto\user",
    "$basePackage\data\remote\dto\group",
    "$basePackage\data\remote\datasource",
    "$basePackage\data\local\entities",
    "$basePackage\data\local\datasource",
    "$basePackage\data\mapper",
    "$basePackage\data\repository",
    
    # Domain layer
    "$basePackage\domain\model",
    "$basePackage\domain\repository",
    "$basePackage\domain\usecase\auth",
    "$basePackage\domain\usecase\post",
    "$basePackage\domain\usecase\comment",
    "$basePackage\domain\usecase\user",
    "$basePackage\domain\usecase\group",
    "$basePackage\domain\usecase\message",
    
    # Presentation layer
    "$basePackage\presentation\navigation",
    "$basePackage\presentation\theme",
    "$basePackage\presentation\components",
    "$basePackage\presentation\features\splash",
    "$basePackage\presentation\features\onboarding",
    "$basePackage\presentation\features\auth\login",
    "$basePackage\presentation\features\auth\register",
    "$basePackage\presentation\features\home",
    "$basePackage\presentation\features\home\components",
    "$basePackage\presentation\features\post\detail",
    "$basePackage\presentation\features\post\create",
    "$basePackage\presentation\features\post\edit",
    "$basePackage\presentation\features\profile",
    "$basePackage\presentation\features\profile\components",
    "$basePackage\presentation\features\messages\list",
    "$basePackage\presentation\features\messages\chat",
    "$basePackage\presentation\features\settings",
    
    # Test directories
    "$testPackage\domain\usecase",
    "$testPackage\data\repository",
    "$testPackage\presentation\viewmodel",
    "$androidTestPackage\di",
    "$androidTestPackage\ui"
)

# Tạo tất cả thư mục
foreach ($dir in $directories) {
    $fullPath = Join-Path $PSScriptRoot $dir
    if (-not (Test-Path $fullPath)) {
        New-Item -ItemType Directory -Path $fullPath -Force | Out-Null
        Write-Host "✅ Created: $dir" -ForegroundColor Cyan
    } else {
        Write-Host "⏭️  Exists: $dir" -ForegroundColor Yellow
    }
}

Write-Host "`n🎉 Hoàn thành! Cấu trúc thư mục đã được tạo." -ForegroundColor Green
Write-Host "📖 Xem chi tiết tại: ANDROID_STRUCTURE_GUIDE.md" -ForegroundColor Magenta
Write-Host "`n📋 Các bước tiếp theo:" -ForegroundColor Yellow
Write-Host "  1. Di chuyển code hiện tại vào cấu trúc mới"
Write-Host "  2. Cài đặt dependencies (Hilt, Retrofit, Room...)"
Write-Host "  3. Implement các base classes trong core/base/"
Write-Host "  4. Setup Dependency Injection trong core/di/"
Write-Host "  5. Tạo domain models và use cases"
Write-Host "  6. Implement repositories và data sources"
Write-Host "  7. Refactor UI screens theo MVVM pattern"
