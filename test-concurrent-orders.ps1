# Test concurrent order creation, verify order number uniqueness

# Define order creation function
function Create-Order {
    param(
        [int]$orderId
    )
    
    $body = @{
        senderName = "Test Sender $orderId"
        senderPhone = "1380013800$orderId"
        senderAddress = "Beijing Chaoyang District"
        receiverName = "Test Receiver $orderId"
        receiverPhone = "1390013900$orderId"
        receiverAddress = "Shanghai Pudong District"
        goodsName = "Test Goods $orderId"
        quantity = 1
        weight = 1.5
        volume = 0.1
        totalFee = 100
        paymentMethod = 1
        status = 1
    } | ConvertTo-Json
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8081/api/order" -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
        Write-Host "Order $orderId created successfully: $($response.StatusCode)"
        return $true
    } catch {
        Write-Host "Error creating order $orderId: $($_.Exception.Message)"
        return $false
    }
}

# Create 10 orders concurrently
$jobs = @()
for ($i = 1; $i -le 10; $i++) {
    $jobs += Start-Job -ScriptBlock ${function:Create-Order} -ArgumentList $i
}

# Wait for all jobs to complete
Write-Host "Waiting for all orders to be created..."
$jobs | Wait-Job | Receive-Job

# Clean up jobs
$jobs | Remove-Job

# Query all orders, check order numbers
Write-Host "\nChecking order numbers..."
$response = Invoke-WebRequest -Uri "http://localhost:8081/api/order/list" -UseBasicParsing
$orders = $response.Content | ConvertFrom-Json

# Extract order numbers and check for duplicates
$orderNumbers = $orders | Select-Object -ExpandProperty orderNo | Where-Object { $_ -like "HM*" }
$uniqueOrderNumbers = $orderNumbers | Select-Object -Unique

Write-Host "Total orders with HM prefix: $($orderNumbers.Count)"
Write-Host "Unique order numbers: $($uniqueOrderNumbers.Count)"

if ($orderNumbers.Count -eq $uniqueOrderNumbers.Count) {
    Write-Host "\n✓ All order numbers are unique!"
} else {
    Write-Host "\n✗ Duplicate order numbers found!"
    $duplicates = $orderNumbers | Group-Object | Where-Object { $_.Count -gt 1 } | Select-Object -ExpandProperty Name
    Write-Host "Duplicate order numbers: $duplicates"
}

# Display latest order numbers
Write-Host "\nLatest order numbers:"
$orders | Where-Object { $_.orderNo -like "HM*" } | Sort-Object -Property id -Descending | Select-Object -First 10 | ForEach-Object {
    Write-Host "Order $($_.id): $($_.orderNo)"
}
