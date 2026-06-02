# Harvest Management Service - Frontend Implementation Guide

## Table of Contents
1. [Overview](#overview)
2. [Base Configuration](#base-configuration)
3. [Authentication](#authentication)
4. [Response Format](#response-format)
5. [Harvest APIs](#harvest-apis)
6. [Requirement APIs](#requirement-apis)
7. [Error Handling](#error-handling)
8. [Example Code](#example-code)

---

## Overview

The Harvest Management Service provides APIs for managing farm harvest listings and merchant requirements with geospatial functionality. All endpoints are RESTful and return JSON responses.

**Current Date Context:** May 27, 2026

---

## Base Configuration

### Base URL
```
http://localhost:8081/api/v1
```

### Required Headers
All requests should include:
```
Content-Type: application/json
X-User-Id: <farmer-id-or-merchant-id>
```

**Note:** The `X-User-Id` header contains the authenticated user's ID (farmer ID for harvests, merchant ID for requirements).

---

## Authentication

The API expects user identification via the `X-User-Id` header. This should contain:
- **For Harvest Operations:** Farmer ID (e.g., `USR1001`)
- **For Requirement Operations:** Merchant ID (e.g., `USR5001`)

```javascript
const headers = {
  'Content-Type': 'application/json',
  'X-User-Id': 'USR1001' // Current authenticated user ID
};
```

---

## Response Format

### Standard Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    // Response payload varies by endpoint
  }
}
```

### Standard Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### HTTP Status Codes
| Status | Meaning |
|--------|---------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 400 | Bad Request - Invalid parameters |
| 404 | Not Found - Resource doesn't exist |
| 500 | Internal Server Error - Server error |

---

## Harvest APIs

### 1. Create Harvest

**Endpoint:** `POST /harvests`

**Request Headers:**
```
Content-Type: application/json
X-User-Id: USR1001
```

**Request Body:**
```json
{
  "title": "Fresh Tomato Harvest",
  "cropType": "TOMATO",
  "quantity": 1000,
  "unit": "KG",
  "pricePerUnit": 150,
  "district": "Kandy",
  "latitude": 7.2906,
  "longitude": 80.6337,
  "harvestDate": "2026-05-25",
  "availableUntil": "2026-06-05",
  "organic": false,
  "description": "Fresh tomatoes from farm"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Harvest created successfully",
  "data": {
    "harvestId": "HAR1AB2CD3"
  }
}
```

**Field Descriptions:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | string | Yes | Harvest title |
| cropType | string | Yes | Type of crop (e.g., TOMATO, CARROT) |
| quantity | number | Yes | Amount of harvest |
| unit | string | Yes | Unit of measurement (e.g., KG, TONS) |
| pricePerUnit | number | Yes | Price per unit |
| district | string | No | District name |
| latitude | number | Yes | Latitude coordinate |
| longitude | number | Yes | Longitude coordinate |
| harvestDate | date | No | Date of harvest (YYYY-MM-DD) |
| availableUntil | date | No | Expiry date (YYYY-MM-DD) |
| organic | boolean | No | Is organic certified |
| description | text | No | Detailed description |

---

### 2. Get Harvest by ID

**Endpoint:** `GET /harvests/{harvestId}`

**Request Parameters:**
| Parameter | Location | Type | Required |
|-----------|----------|------|----------|
| harvestId | URL path | string | Yes |

**Example:**
```
GET /harvests/HAR1AB2CD3
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Harvest retrieved successfully",
  "data": {
    "harvestId": "HAR1AB2CD3",
    "farmerId": "USR1001",
    "title": "Fresh Tomato Harvest",
    "cropType": "TOMATO",
    "quantity": 1000,
    "unit": "KG",
    "pricePerUnit": 150,
    "district": "Kandy",
    "latitude": 7.2906,
    "longitude": 80.6337,
    "harvestDate": "2026-05-25",
    "availableUntil": "2026-06-05",
    "organic": false,
    "description": "Fresh tomatoes from farm",
    "status": "ACTIVE",
    "distanceKm": null
  }
}
```

**Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Harvest not found",
  "data": null
}
```

---

### 3. Update Harvest

**Endpoint:** `PUT /harvests/{harvestId}`

**Request Headers:**
```
Content-Type: application/json
X-User-Id: USR1001
```

**Request Body:**
```json
{
  "title": "Fresh Tomato Harvest - Updated",
  "cropType": "TOMATO",
  "quantity": 1200,
  "unit": "KG",
  "pricePerUnit": 160,
  "district": "Kandy",
  "latitude": 7.2906,
  "longitude": 80.6337,
  "harvestDate": "2026-05-25",
  "availableUntil": "2026-06-10",
  "organic": false,
  "description": "Fresh tomatoes from farm - Updated quantity"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Harvest updated successfully",
  "data": {
    "harvestId": "HAR1AB2CD3",
    "farmerId": "USR1001",
    "title": "Fresh Tomato Harvest - Updated",
    "cropType": "TOMATO",
    "quantity": 1200,
    "unit": "KG",
    "pricePerUnit": 160,
    "district": "Kandy",
    "latitude": 7.2906,
    "longitude": 80.6337,
    "harvestDate": "2026-05-25",
    "availableUntil": "2026-06-10",
    "organic": false,
    "description": "Fresh tomatoes from farm - Updated quantity",
    "status": "ACTIVE",
    "distanceKm": null
  }
}
```

---

### 4. Delete Harvest

**Endpoint:** `DELETE /harvests/{harvestId}`

**Example:**
```
DELETE /harvests/HAR1AB2CD3
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Harvest deleted successfully",
  "data": null
}
```

**Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Harvest not found with ID: HAR1AB2CD3",
  "data": null
}
```

---

### 5. Search Harvests

**Endpoint:** `GET /harvests/search`

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| cropType | string | No | Filter by crop type (e.g., TOMATO) |
| district | string | No | Filter by district |
| minPrice | number | No | Minimum price per unit |
| maxPrice | number | No | Maximum price per unit |
| organic | boolean | No | Filter by organic status (true/false) |

**Examples:**
```
GET /harvests/search?cropType=TOMATO
GET /harvests/search?district=Kandy
GET /harvests/search?cropType=CARROT&minPrice=100&maxPrice=200
GET /harvests/search?organic=true
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Search completed successfully",
  "data": [
    {
      "harvestId": "HAR1AB2CD3",
      "farmerId": "USR1001",
      "title": "Fresh Tomato Harvest",
      "cropType": "TOMATO",
      "quantity": 1000,
      "unit": "KG",
      "pricePerUnit": 150,
      "district": "Kandy",
      "latitude": 7.2906,
      "longitude": 80.6337,
      "harvestDate": "2026-05-25",
      "availableUntil": "2026-06-05",
      "organic": false,
      "description": "Fresh tomatoes from farm",
      "status": "ACTIVE",
      "distanceKm": null
    },
    {
      "harvestId": "HAR2XY9ZA1",
      "farmerId": "USR1002",
      "title": "Organic Cherry Tomatoes",
      "cropType": "TOMATO",
      "quantity": 500,
      "unit": "KG",
      "pricePerUnit": 200,
      "district": "Colombo",
      "latitude": 6.9271,
      "longitude": 79.8612,
      "harvestDate": "2026-05-24",
      "availableUntil": "2026-06-02",
      "organic": true,
      "description": "Premium organic cherry tomatoes",
      "status": "ACTIVE",
      "distanceKm": null
    }
  ]
}
```

---

### 6. Search Nearby Harvests

**Endpoint:** `GET /harvests/nearby`

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| latitude | number | Yes | - | User's latitude |
| longitude | number | Yes | - | User's longitude |
| radiusKm | integer | No | 25 | Search radius in kilometers |
| cropType | string | No | - | Filter by crop type |

**Examples:**
```
GET /harvests/nearby?latitude=7.2906&longitude=80.6337&radiusKm=25
GET /harvests/nearby?latitude=7.2906&longitude=80.6337&radiusKm=50&cropType=TOMATO
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Nearby search completed successfully",
  "data": [
    {
      "harvestId": "HAR1AB2CD3",
      "farmerId": "USR1001",
      "title": "Fresh Tomato Harvest",
      "cropType": "TOMATO",
      "quantity": 1000,
      "unit": "KG",
      "pricePerUnit": 150,
      "district": "Kandy",
      "latitude": 7.2906,
      "longitude": 80.6337,
      "harvestDate": "2026-05-25",
      "availableUntil": "2026-06-05",
      "organic": false,
      "description": "Fresh tomatoes from farm",
      "status": "ACTIVE",
      "distanceKm": 2.4
    },
    {
      "harvestId": "HAR3MN4OP5",
      "farmerId": "USR1003",
      "title": "Fresh Beans",
      "cropType": "BEANS",
      "quantity": 500,
      "unit": "KG",
      "pricePerUnit": 120,
      "district": "Matale",
      "latitude": 7.4675,
      "longitude": 80.6234,
      "harvestDate": "2026-05-25",
      "availableUntil": "2026-06-10",
      "organic": true,
      "description": "Fresh hill country beans",
      "status": "ACTIVE",
      "distanceKm": 21.5
    }
  ]
}
```

---

### 7. Get Farmer's Harvests

**Endpoint:** `GET /harvests/farmer/{farmerId}`

**Request Parameters:**
| Parameter | Location | Type | Required |
|-----------|----------|------|----------|
| farmerId | URL path | string | Yes |

**Example:**
```
GET /harvests/farmer/USR1001
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Farmer harvests retrieved successfully",
  "data": [
    {
      "harvestId": "HAR1AB2CD3",
      "farmerId": "USR1001",
      "title": "Fresh Tomato Harvest",
      "cropType": "TOMATO",
      "quantity": 1000,
      "unit": "KG",
      "pricePerUnit": 150,
      "district": "Kandy",
      "latitude": 7.2906,
      "longitude": 80.6337,
      "harvestDate": "2026-05-25",
      "availableUntil": "2026-06-05",
      "organic": false,
      "description": "Fresh tomatoes from farm",
      "status": "ACTIVE",
      "distanceKm": null
    },
    {
      "harvestId": "HAR2XY9ZA1",
      "farmerId": "USR1001",
      "title": "Fresh Beans",
      "cropType": "BEANS",
      "quantity": 300,
      "unit": "KG",
      "pricePerUnit": 120,
      "district": "Kandy",
      "latitude": 7.3906,
      "longitude": 80.7337,
      "harvestDate": "2026-05-26",
      "availableUntil": "2026-06-10",
      "organic": true,
      "description": "Organic fresh beans",
      "status": "ACTIVE",
      "distanceKm": null
    }
  ]
}
```

---

### 8. Update Harvest Status

**Endpoint:** `PUT /harvests/{harvestId}/status`

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | Yes | New status (DRAFT, ACTIVE, RESERVED, SOLD, EXPIRED, CANCELLED) |

**Example:**
```
PUT /harvests/HAR1AB2CD3/status?status=SOLD
```

**Valid Status Values:**
| Status | Description |
|--------|-------------|
| DRAFT | Not published |
| ACTIVE | Open for bidding |
| RESERVED | Reserved |
| SOLD | Sold |
| EXPIRED | Expired |
| CANCELLED | Cancelled |

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Harvest status updated successfully",
  "data": null
}
```

---

## Requirement APIs

### 1. Create Requirement

**Endpoint:** `POST /requirements`

**Request Headers:**
```
Content-Type: application/json
X-User-Id: USR5001
```

**Request Body:**
```json
{
  "cropType": "CARROT",
  "requiredQuantity": 500,
  "unit": "KG",
  "expectedPrice": 120,
  "district": "Colombo",
  "latitude": 6.9271,
  "longitude": 79.8612,
  "requiredBefore": "2026-06-15",
  "description": "Need export quality carrots"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Requirement created successfully",
  "data": {
    "requirementId": "REQ1AB2CD3"
  }
}
```

**Field Descriptions:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| cropType | string | Yes | Type of crop needed |
| requiredQuantity | number | Yes | Quantity needed |
| unit | string | Yes | Unit of measurement (e.g., KG, TONS) |
| expectedPrice | number | Yes | Expected price per unit |
| district | string | No | District name |
| latitude | number | Yes | Latitude coordinate |
| longitude | number | Yes | Longitude coordinate |
| requiredBefore | date | Yes | Deadline (YYYY-MM-DD) |
| description | text | No | Detailed description |

---

### 2. Get Requirement by ID

**Endpoint:** `GET /requirements/{requirementId}`

**Example:**
```
GET /requirements/REQ1AB2CD3
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Requirement retrieved successfully",
  "data": {
    "requirementId": "REQ1AB2CD3",
    "merchantId": "USR5001",
    "cropType": "CARROT",
    "requiredQuantity": 500,
    "unit": "KG",
    "expectedPrice": 120,
    "district": "Colombo",
    "latitude": 6.9271,
    "longitude": 79.8612,
    "requiredBefore": "2026-06-15",
    "description": "Need export quality carrots",
    "status": "OPEN",
    "distanceKm": null
  }
}
```

---

### 3. Update Requirement

**Endpoint:** `PUT /requirements/{requirementId}`

**Request Headers:**
```
Content-Type: application/json
X-User-Id: USR5001
```

**Request Body:**
```json
{
  "cropType": "CARROT",
  "requiredQuantity": 750,
  "unit": "KG",
  "expectedPrice": 130,
  "district": "Colombo",
  "latitude": 6.9271,
  "longitude": 79.8612,
  "requiredBefore": "2026-06-20",
  "description": "Need export quality carrots - increased quantity"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Requirement updated successfully",
  "data": {
    "requirementId": "REQ1AB2CD3",
    "merchantId": "USR5001",
    "cropType": "CARROT",
    "requiredQuantity": 750,
    "unit": "KG",
    "expectedPrice": 130,
    "district": "Colombo",
    "latitude": 6.9271,
    "longitude": 79.8612,
    "requiredBefore": "2026-06-20",
    "description": "Need export quality carrots - increased quantity",
    "status": "OPEN",
    "distanceKm": null
  }
}
```

---

### 4. Delete Requirement

**Endpoint:** `DELETE /requirements/{requirementId}`

**Example:**
```
DELETE /requirements/REQ1AB2CD3
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Requirement deleted successfully",
  "data": null
}
```

---

### 5. Search Nearby Requirements

**Endpoint:** `GET /requirements/nearby`

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| latitude | number | Yes | - | Farmer's latitude |
| longitude | number | Yes | - | Farmer's longitude |
| radiusKm | integer | No | 25 | Search radius in kilometers |

**Example:**
```
GET /requirements/nearby?latitude=7.2906&longitude=80.6337&radiusKm=50
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Nearby search completed successfully",
  "data": [
    {
      "requirementId": "REQ1AB2CD3",
      "merchantId": "USR5001",
      "cropType": "CARROT",
      "requiredQuantity": 500,
      "unit": "KG",
      "expectedPrice": 120,
      "district": "Colombo",
      "latitude": 6.9271,
      "longitude": 79.8612,
      "requiredBefore": "2026-06-15",
      "description": "Need export quality carrots",
      "status": "OPEN",
      "distanceKm": 35.2
    },
    {
      "requirementId": "REQ2XY9ZA1",
      "merchantId": "USR5002",
      "cropType": "TOMATO",
      "requiredQuantity": 1000,
      "unit": "KG",
      "expectedPrice": 150,
      "district": "Matale",
      "latitude": 7.4675,
      "longitude": 80.6234,
      "requiredBefore": "2026-06-10",
      "description": "Restaurant supply tomatoes",
      "status": "OPEN",
      "distanceKm": 21.8
    }
  ]
}
```

---

### 6. Get Merchant's Requirements

**Endpoint:** `GET /requirements/merchant/{merchantId}`

**Example:**
```
GET /requirements/merchant/USR5001
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Merchant requirements retrieved successfully",
  "data": [
    {
      "requirementId": "REQ1AB2CD3",
      "merchantId": "USR5001",
      "cropType": "CARROT",
      "requiredQuantity": 500,
      "unit": "KG",
      "expectedPrice": 120,
      "district": "Colombo",
      "latitude": 6.9271,
      "longitude": 79.8612,
      "requiredBefore": "2026-06-15",
      "description": "Need export quality carrots",
      "status": "OPEN",
      "distanceKm": null
    }
  ]
}
```

---

### 7. Update Requirement Status

**Endpoint:** `PUT /requirements/{requirementId}/status`

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | Yes | New status (OPEN, IN_NEGOTIATION, FULFILLED, EXPIRED, CANCELLED) |

**Example:**
```
PUT /requirements/REQ1AB2CD3/status?status=FULFILLED
```

**Valid Status Values:**
| Status | Description |
|--------|-------------|
| OPEN | Accepting bids |
| IN_NEGOTIATION | Negotiation ongoing |
| FULFILLED | Completed |
| EXPIRED | Expired |
| CANCELLED | Cancelled |

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Requirement status updated successfully",
  "data": null
}
```

---

## Error Handling

### Common Error Scenarios

**400 - Bad Request (Invalid Parameters)**
```json
{
  "success": false,
  "message": "Failed to create harvest: Invalid coordinates",
  "data": null
}
```

**404 - Not Found**
```json
{
  "success": false,
  "message": "Harvest not found with ID: HAR999",
  "data": null
}
```

**500 - Internal Server Error**
```json
{
  "success": false,
  "message": "Error retrieving harvest: Database connection failed",
  "data": null
}
```

### Error Handling Best Practices

Always check the `success` field:
```javascript
const response = await fetch('/api/v1/harvests', options);
const result = await response.json();

if (!result.success) {
  console.error('Error:', result.message);
  // Handle error appropriately
} else {
  console.log('Success:', result.data);
  // Process response data
}
```

---

## Example Code

### JavaScript/TypeScript Examples

#### 1. Create Harvest (Fetch API)
```javascript
async function createHarvest(farmerId, harvestData) {
  const response = await fetch('http://localhost:8081/api/v1/harvests', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-User-Id': farmerId
    },
    body: JSON.stringify({
      title: 'Fresh Tomato Harvest',
      cropType: 'TOMATO',
      quantity: 1000,
      unit: 'KG',
      pricePerUnit: 150,
      district: 'Kandy',
      latitude: 7.2906,
      longitude: 80.6337,
      harvestDate: '2026-05-25',
      availableUntil: '2026-06-05',
      organic: false,
      description: 'Fresh tomatoes from farm'
    })
  });


  ### 8. Get Harvests by Status

  **Endpoint:** `GET /harvests/status/{status}`

  **Request Parameters:**
  | Parameter | Location | Type | Required |
  |-----------|----------|------|----------|
  | status | URL path | string | Yes |

  **Example:**
  ```
  GET /harvests/status/DRAFT
  GET /harvests/status/ACTIVE
  GET /harvests/status/RESERVED
  GET /harvests/status/SOLD
  GET /harvests/status/EXPIRED
  GET /harvests/status/CANCELLED
  ```

  **Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Harvests retrieved successfully",
    "data": [
      {
        "harvestId": "HAR1AB2CD3",
        "farmerId": "USR1001",
        "title": "Fresh Tomato Harvest",
        "cropType": "TOMATO",
        "quantity": 1000,
        "unit": "KG",
        "pricePerUnit": 150,
        "district": "Kandy",
        "latitude": 7.2906,
        "longitude": 80.6337,
        "harvestDate": "2026-05-25",
        "availableUntil": "2026-06-05",
        "organic": false,
        "description": "Fresh tomatoes from farm",
        "status": "ACTIVE",
        "distanceKm": null
      }
    ]
  }
  ```

  ### 9. Get Current User's Harvests

  **Endpoint:** `GET /harvests/me`

  **Request Headers:**
  ```
  Content-Type: application/json
  X-User-Id: USR1001
  ```

  **Optional Query Parameters:**
  | Parameter | Type | Required | Description |
  |-----------|------|----------|-------------|
  | status | string | No | Filter the authenticated user's harvests by status |

  **Examples:**
  ```
  GET /harvests/me
  GET /harvests/me?status=DRAFT
  GET /harvests/me?status=ACTIVE
  GET /harvests/me?status=SOLD
  ```

  **Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Harvests retrieved successfully",
    "data": [
      {
        "harvestId": "HAR1AB2CD3",
        "farmerId": "USR1001",
        "title": "Fresh Tomato Harvest",
        "cropType": "TOMATO",
        "quantity": 1000,
        "unit": "KG",
        "pricePerUnit": 150,
        "district": "Kandy",
        "latitude": 7.2906,
        "longitude": 80.6337,
        "harvestDate": "2026-05-25",
        "availableUntil": "2026-06-05",
        "organic": false,
        "description": "Fresh tomatoes from farm",
        "status": "DRAFT",
        "distanceKm": null
      }
    ]
  }
  ```
  const result = await response.json();
  ### 10. Update Harvest Status
    console.log('Harvest created:', result.data.harvestId);
    return result.data;
  } else {
    console.error('Error:', result.message);
    throw new Error(result.message);
  }
}

// Usage
try {
  const harvest = await createHarvest('USR1001', {/* data */});
} catch (error) {
  console.error('Failed to create harvest:', error);
}
```

#### 2. Search Nearby Harvests (Axios)
```javascript
import axios from 'axios';

async function searchNearbyHarvests(latitude, longitude, radiusKm = 25, cropType = null) {
  try {
    const params = {
      latitude,
      longitude,
      radiusKm
    };
    
    if (cropType) {
      params.cropType = cropType;
    }

    const response = await axios.get('http://localhost:8081/api/v1/harvests/nearby', {
      params,
      headers: {
        'X-User-Id': 'USR1001'
      }
    });

    if (response.data.success) {
      console.log('Found harvests:', response.data.data);
      return response.data.data;
    } else {
      throw new Error(response.data.message);
    }
  } catch (error) {
    console.error('Search failed:', error);
    throw error;
  }
}

// Usage
const harvests = await searchNearbyHarvests(7.2906, 80.6337, 50, 'TOMATO');
```

#### 3. Get Current User's Harvests
```javascript
async function getFarmerHarvests(farmerId) {
  try {
    const response = await fetch(
      `http://localhost:8081/api/v1/harvests/farmer/${farmerId}`,
      {
        headers: {
          'X-User-Id': farmerId
        }
      }
    );

    const result = await response.json();
    
    if (result.success) {
      return result.data; // Array of harvests
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Failed to fetch harvests:', error);
    throw error;
  }
}

// Usage
const myHarvests = await getFarmerHarvests('USR1001');
myHarvests.forEach(harvest => {
  console.log(`${harvest.title} - ${harvest.cropType} (${harvest.quantity} ${harvest.unit})`);
});
```

#### 4. Update Harvest Status
```javascript
async function updateHarvestStatus(harvestId, newStatus) {
  try {
    const response = await fetch(
      `http://localhost:8081/api/v1/harvests/${harvestId}/status?status=${newStatus}`,
      {
        method: 'PUT',
        headers: {
          'X-User-Id': 'USR1001'
        }
      }
    );

    const result = await response.json();
    
    if (result.success) {
      console.log(`Harvest status updated to: ${newStatus}`);
      return true;
    } else {
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('Failed to update status:', error);
    throw error;
  }
}

// Usage
await updateHarvestStatus('HAR1AB2CD3', 'SOLD');
```

#### 5. Create Requirement with Error Handling
```javascript
class HarvestAPI {
  constructor(baseUrl, userId) {
    this.baseUrl = baseUrl;
    this.userId = userId;
  }

  async createRequirement(requirementData) {
    try {
      const response = await fetch(`${this.baseUrl}/requirements`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': this.userId
        },
        body: JSON.stringify(requirementData)
      });

      const result = await response.json();

      if (!result.success) {
        throw new Error(result.message);
      }

      return result.data;
    } catch (error) {
      console.error('API Error:', error.message);
      throw error;
    }
  }

  async searchNearbyRequirements(latitude, longitude, radiusKm = 25) {
    const response = await fetch(
      `${this.baseUrl}/requirements/nearby?latitude=${latitude}&longitude=${longitude}&radiusKm=${radiusKm}`,
      {
        headers: {
          'X-User-Id': this.userId
        }
      }
    );

    const result = await response.json();
    
    if (!result.success) {
      throw new Error(result.message);
    }

    return result.data;
  }
}

// Usage
const api = new HarvestAPI('http://localhost:8081/api/v1', 'USR5001');

try {
  const requirement = await api.createRequirement({
    cropType: 'CARROT',
    requiredQuantity: 500,
    unit: 'KG',
    expectedPrice: 120,
    district: 'Colombo',
    latitude: 6.9271,
    longitude: 79.8612,
    requiredBefore: '2026-06-15',
    description: 'Need export quality carrots'
  });

  console.log('Created requirement:', requirement.requirementId);

  const nearbyRequirements = await api.searchNearbyRequirements(7.2906, 80.6337, 50);
  console.log('Found requirements:', nearbyRequirements.length);
} catch (error) {
  console.error('Operation failed:', error);
}
```

#### 6. React Hook for Harvest Management
```javascript
import { useState, useCallback } from 'react';

export function useHarvest(baseUrl, userId) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const createHarvest = useCallback(async (harvestData) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`${baseUrl}/harvests`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': userId
        },
        body: JSON.stringify(harvestData)
      });

      const result = await response.json();
      
      if (!result.success) {
        throw new Error(result.message);
      }

      return result.data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [baseUrl, userId]);

  const searchNearby = useCallback(async (latitude, longitude, radiusKm, cropType) => {
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams({
        latitude,
        longitude,
        radiusKm
      });

      if (cropType) {
        params.append('cropType', cropType);
      }

      const response = await fetch(
        `${baseUrl}/harvests/nearby?${params}`,
        {
          headers: {
            'X-User-Id': userId
          }
        }
      );

      const result = await response.json();
      
      if (!result.success) {
        throw new Error(result.message);
      }

      return result.data;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [baseUrl, userId]);

  return { createHarvest, searchNearby, loading, error };
}

// Usage in React component
function HarvestListingForm() {
  const { createHarvest, loading, error } = useHarvest(
    'http://localhost:8081/api/v1',
    'USR1001'
  );

  const handleSubmit = async (formData) => {
    try {
      const result = await createHarvest(formData);
      console.log('Harvest created:', result.harvestId);
      // Show success message
    } catch (err) {
      console.error('Error:', error);
      // Show error message
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* Form fields */}
      <button type="submit" disabled={loading}>
        {loading ? 'Creating...' : 'Create Harvest'}
      </button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </form>
  );
}
```

---

## Testing the APIs

### Using cURL

**Create Harvest:**
```bash
curl -X POST http://localhost:8081/api/v1/harvests \
  -H "Content-Type: application/json" \
  -H "X-User-Id: USR1001" \
  -d '{
    "title": "Fresh Tomato Harvest",
    "cropType": "TOMATO",
    "quantity": 1000,
    "unit": "KG",
    "pricePerUnit": 150,
    "district": "Kandy",
    "latitude": 7.2906,
    "longitude": 80.6337,
    "harvestDate": "2026-05-25",
    "availableUntil": "2026-06-05",
    "organic": false,
    "description": "Fresh tomatoes from farm"
  }'
```

**Search Nearby Harvests:**
```bash
curl -X GET "http://localhost:8081/api/v1/harvests/nearby?latitude=7.2906&longitude=80.6337&radiusKm=25&cropType=TOMATO" \
  -H "X-User-Id: USR1001"
```

**Get Farmer's Harvests:**
```bash
curl -X GET http://localhost:8081/api/v1/harvests/farmer/USR1001 \
  -H "X-User-Id: USR1001"
```

---

## Swagger/OpenAPI Documentation

The API documentation is available at:
```
http://localhost:8081/swagger-ui.html
```

This provides an interactive interface to test all endpoints directly from your browser.

---

## Summary

- **Base URL:** `http://localhost:8081/api/v1`
- **Content-Type:** Always `application/json`
- **Authentication:** Use `X-User-Id` header with farmer/merchant ID
- **Response Format:** All responses wrap data in `{ success, message, data }` object
- **Geospatial Features:** Nearby search with kilometer radius support
- **Status Management:** Track harvest and requirement lifecycle

For more details, refer to the Swagger documentation or contact the backend team.
