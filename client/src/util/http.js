import axios from 'axios'
import {Message} from 'element-ui'

// create an axios instance
const service = axios.create({
  baseURL: process.env.BASE_API, // api 的 base_url
  headers: {},
  timeout: 60000 // request timeout
})

const dealUrl = (url, params) => {
  if (!url) {
    throw new Error('参数不能为空')
  }
  const index = url.indexOf('?')
  if (index > -1) {
    url = url.substring(0, index)
    const p = {}
    url.substring(index + 1).split('&').forEach(e => {
      if (e && e.trim()) {
        const val = e.trim().split('=')
        p[val[0]] = val[1]
      }
    })
    console.log(dealParams(url, params, p))
    return dealParams(url, params, p)
  }
  return dealParams(url, params)
}

const dealParams = (url, params, other) => {
  let ps = {
    ...(other || {}),
    ...(params || {})
  }
  while (true) {
    const regx = /{(.+?)\}/g
    const match = regx.exec(url)
    if (!match) {
      break
    }
    url = url.substring(0, match.index) + ps[match[1]] + url.substring(match.index + match[0].length)
  }
  let pstr = ''
  Object.keys(ps).forEach(e => {
    pstr += `&${e}=${ps[e]}`
  })
  if (pstr) {
    return url + '?' + pstr.substring(1)
  }
  return url
}

const dealResult = res => {
  if (res.status === 200) {
    return res.data
  } else {
    return new Error('请求异常, ' + res.status)
  }
}

const http = (url, params, data, method) => {
  method = (method || '').toUpperCase()
  if (method === 'GET') {
    return service.get(dealUrl(url, params)).then(dealResult)
  } else if (method === 'POST') {
    return service.post(dealUrl(url, params), data).then(dealResult)
  } else if (method === 'PUT') {
    return service.put(dealUrl(url, params), data).then(dealResult)
  } else if (method === 'DELETE') {
    return service.delete(dealUrl(url, params), data).then(dealResult)
  } else if (method === 'HEAD') {
    return service.head(dealUrl(url, params)).then(dealResult)
  } else {
    return service.options(dealUrl(url, params)).then(dealResult)
  }
}

// request interceptor
service.interceptors.request.use(
  config => {
    // Do something before request is sent
    return config
  },
  error => {
    // Do something with request error
    console.log(error) // for debug
    Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => response,
  error => {
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default http
