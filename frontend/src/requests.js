import axios from 'axios'
import NODE_ENV from "./config";


export let URL;
if (NODE_ENV === "DEV") {
    URL = "http://localhost:8080";
} else {
    URL = "“http://backend:8080”"
}

export const authorizeByVk = async (code) => {
    return await axios.post(URL + '/api/auth/oauth/authorizeByVk', {code: code})
}

export const AuthInfo = async (jwt) => {
    return await axios.get(URL + '/api/auth/info', {
        headers: {
            'Authorization': `Bearer ${jwt}`,
        }
    })
}

export const cryptoGetAllType = async (jwt) => {
    return axios.get(URL + '/api/crypto/cryptoData/getAllByType?type=BTC', {
        headers: {
            'Authorization': `Bearer ${jwt}`,
        },
    })
}

export const cryptoPostData = async (jwt, data) => {
    return await axios.post(URL + '/api/crypto/cryptoData', data, {
        headers: {
            'Authorization': `Bearer ${jwt}`
        }
    })
}

export const cryptoDeleteData = async (jwt, id) => {
    return await axios.delete(URL + `/api/crypto/cryptoData?id=${id}`, {
        headers: {
            'Authorization': `Bearer ${jwt}`
        }
    })
}

export const cryptoGetGeneratePrivateKey = async (jwt) => {
    console.log("eu")
    return await axios.get(URL + '/api/crypto/cryptoData/generatePrivateKeyByType?type=BTC', {
        headers: {
            'Authorization': `Bearer ${jwt}`
        }
    })
}

export const sendTransaction = async (jwt,data) => {
    return await axios.post(URL + '/api/crypto/sendBtcTransaction', data, {
        headers: {
            'Authorization': `Bearer ${jwt}`
        }
    })
}