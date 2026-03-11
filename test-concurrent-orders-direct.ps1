# Test concurrent order creation

# Create 5 orders sequentially to test order number generation
for ($i = 1; $i -le 5; $i++) {
    $body = @{
        senderName = "Test Sender $i"
        senderPhone = "1380013800$i"
        senderAddress = "Beijing"
        receiverName = "Test Receiver $i"
        receiverPhone = "1390013900$i"
        receiverAddress = "Shanghai"
        goodsName = "Test Goods $i"
        quantity = 1
        weight = 1.5
        volume = 0.1
        totalFee = 100
        paymentMethod = 1
        status = 1
    } | ConvertTo-Json
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8081/api/order" -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
        Write-Host "Order $i created successfully: $($response.StatusCode)"
    } catch {
        Write-Host "Error creating order $i: $($_.Exception.Message)"
    }
}

# Check the results
Write-Host "\nChecking order numbers..."
$response = Invoke-WebRequest -Uri "http://localhost:8081/api/order/list" -UseBasicParsing
$orders = $response.Content | ConvertFrom-Json

# Get HM-prefixed order numbers
$hmOrders = $orders | Where-Object { $_.orderNo -like "HM*" } | Sort-Object -Property id -Descending

Write-Host "Total HM orders: $($hmOrders.Count)"
Write-Host "Latest order numbers:"
$hmOrders | Select-Object -First 15 | ForEach-Object {
    Write-Host "Order $($_.id): $($_.orderNo)"
}

# Check for duplicates
$orderNumbers = $hmOrders | Select-Object -ExpandProperty orderNo
$uniqueOrderNumbers = $orderNumbers | Select-Object -Unique

if ($orderNumbers.Count -eq $uniqueOrderNumbers.Count) {
    Write-Host "\n✓ All order numbers are unique!"
} else {
    Write-Host "\n✗ Duplicate order numbers found!"
    $duplicates = $orderNumbers | Group-Object | Where-Object { $_.Count -gt 1 } | Select-Object -ExpandProperty Name
    Write-Host "Duplicate order numbers: $duplicates"
}
