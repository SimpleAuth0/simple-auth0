import defaultSettings from '@/settings'

const title = defaultSettings.title || 'Simple Auth0'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
