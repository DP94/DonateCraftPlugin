import path from "path";

const express = require('express');
const router = express.Router();

router.get('/', (request: any, response: any) => {
    response.sendFile(path.resolve('build/public', 'index.html'));
});

module.exports = router;