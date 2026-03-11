# Test order creation

# Create 5 orders
for ($i = 1; $i -le 5; $i++) {
    $body = '{"senderName":"Test Sender ' + $i + '","senderPhone":"1380013800' + $i + '","senderAddress":"Beijing","receiverName":"Test Receiver ' + $i + '","receiverPhone":"1390013900' + $i + '","receiverAddress":"Shanghai","goodsName":"Test Goods ' + $i + '","quantity":1,"weight":1.5,"volume":0.1,"totalFee":100,"paymentMethod":1,"status":1}'
    
    Invoke-WebRequest -Uri "http://localhost:8081/api/order" -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
    Write-Host "Created order $i"
}

# Check results
Write-Host "\nChecking orders..."
$response = Invoke-WebRequest -Uri "http://localhost:8081/api/order/list" -UseBasicParsing
$content = $response.Content
Write-Host "Response received"
Write-Host "Content length: $($content.Length)"
